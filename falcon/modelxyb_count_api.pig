IMPORT 'utils/include.pig';

----- api count  api被请求数


count_result = foreach data generate user_info.user_name, request_info.request_path, event_id.ip_address, request_info.request_method;
count_result_group = group count_result by (user_name, request_path, ip_address, request_method);
api_result = foreach count_result_group generate group as rst, '$year-$month-$day' as date, COUNT(count_result) as count;
flatten_tuple = foreach api_result generate Flatten(rst), date, count;
count_end_tuple = foreach flatten_tuple generate $0 as user, $1 as path, $2 as ip, $3 as method, $4 as date, $5 as count;
describe count_end_tuple
dump count_end_tuple
--store count_end_tuple into 'model_monitor_xyb_api/apicount' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');

