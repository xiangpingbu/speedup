IMPORT 'utils/include.pig';
/*
	执行命令：pig -p year=2017 -p month=04 -p day=11 -p variable=personalLiveJoin  modelxyb_count_variable.pig
    variable为request的form_params里的是number的参数名,personalLiveJoin,clientGender,personalEducation,personalLiveCase
    
    "form_params":"creditQueryTimes:{2};clientGender:{2};personalEducation:{4};personalLiveJoin:{2,};
    personalYearIncome:{7.00};creditLimit:{0.00};creditUtilization:{0.00000};age:{47};
*/

	-----计算 personalLiveJoin, clientGender, personalEducation, personalLiveCase

data = LOAD '/topics/kafka_lookup_event_v1/year=$year/month=$month/day=$day/api_path=model_xyb' USING AvroStorage();
data = filter data by response_info.response_code == 200;
describe data;
form_params_result = foreach data  generate request_info.form_params;
form_params_parse_result = foreach form_params_result generate pyudf.regx_category(form_params, 1, '$variable');
form_params_parse_result = foreach form_params_parse_result generate val;
variable_count = foreach (group form_params_parse_result by val) generate group as value, COUNT(form_params_parse_result) as count, '$year-$month-$day' as date, '$variable' as category ;
dump variable_count
--store personalLiveJoin_count into 'model_monitor_xyb_stat/bar_chart_data' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');





	

