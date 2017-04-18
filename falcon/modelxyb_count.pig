register '/home/techang/avro-mapred-1.7.5.jar';
REGISTER '/home/techang/piggybank-0.16.0.jar';
register '/home/techang/es-hadoop.jar';
register '/home/techang/datafu-pig-incubating-1.3.2.jar';
register 'xybudf.py' using  jython  as pyudf;
DEFINE Quantile datafu.pig.stats.Quantile('0.1', '0.2', '0.3', '0.4', '0.5', '0.6', '0.7', '0.8', '0.9', '0.95', '0.99');
data = LOAD '/topics/kafka_lookup_event_v1/year=$year/month=$month/day=$day/api_path=model_xyb' USING AvroStorage();
data = filter data by response_info.response_code == 200;
describe data;


	-----计算personalLiveJoin, clientGender, personalEducation, personalLiveCase, model/xyb api 次数



form_params_result = foreach data  generate request_info.form_params;
form_params_parse_result = foreach form_params_result generate pyudf.regx_category(form_params, 1);
form_params_parse_result = foreach form_params_parse_result generate category.clientGender, category.personalEducation, category.personalLiveCase,  category.personalLiveJoin;
personalLiveJoin_count = foreach (group form_params_parse_result by personalLiveJoin) generate group as value, COUNT(form_params_parse_result) as count, '$year$month$day' as date, 'personalLiveJoin' as category ;
dump personalLiveJoin_count
--store personalLiveJoin_count into 'model_monitor_xyb_stat/bar_chart_data' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');
clientGender_count = foreach (group form_params_parse_result by clientGender) generate group as value, COUNT(form_params_parse_result) as count, '$year$month$day' as date, 'clientGender' as category ;
dump clientGender_count
--store clientGender_count into 'model_monitor_xyb_stat/bar_chart_data' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');
personalEducation_count = foreach (group form_params_parse_result by personalEducation) generate group as value, COUNT(form_params_parse_result) as count, '$year$month$day' as date, 'personalEducation' as category ;
dump personalEducation_count
--store personalEducation_count into 'model_monitor_xyb_stat/bar_chart_data' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');
personalLiveCase_count = foreach (group form_params_parse_result by personalLiveCase) generate group as value, COUNT(form_params_parse_result) as count, '$year$month$day' as date, 'personalLiveCase' as category ;
dump personalLiveCase_count
--store personalLiveCase_count into 'model_monitor_xyb_stat/bar_chart_data' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');


----- api count 


count_result = foreach data generate user_info.user_name, request_info.request_path, event_id.ip_address, request_info.request_method;
count_result_group = group count_result by (user_name, request_path, ip_address, request_method);
api_result = foreach count_result_group generate group as rst, '$year$month$day' as date, COUNT(count_result) as count;
flatten_tuple = foreach api_result generate Flatten(rst), date, count;
count_end_tuple = foreach flatten_tuple generate $0 as user, $1 as path, $2 as ip, $3 as method, $4 as date, $5 as count;
describe count_end_tuple
dump count_end_tuple
--store count_end_tuple into 'model_monitor_xyb_api/apicount' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');





	

