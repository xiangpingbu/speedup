register '/home/techang/avro-mapred-1.7.5.jar';
REGISTER '/home/techang/piggybank-0.16.0.jar';
register '/home/techang/es-hadoop.jar';
register '/home/techang/datafu-pig-incubating-1.3.2.jar';
register 'xybudf.py' using  jython  as pyudf;
DEFINE Quantile datafu.pig.stats.Quantile('0.1', '0.2', '0.3', '0.4', '0.5', '0.6', '0.7', '0.8', '0.9', '0.95', '0.99');
data = LOAD '/topics/kafka_lookup_event_v1/year=$year/month=$month/day=$day/api_path=model_xyb' USING AvroStorage();
data = filter data by response_info.response_code == 200;

	------计算creditLimit, personalYearIncome, age, creditQuerytimes, creditUtilization 的percentile
	
form_params_result = foreach data  generate request_info.form_params;
form_params_parse_result = foreach form_params_result generate pyudf.regx_number(form_params, 0);

has_null_result = foreach form_params_parse_result  generate (double)num.credit_limit as (val:double);
credit_limit_no_null_result_percentile = FOREACH ( GROUP has_null_result all) {
  	no_null_result = filter has_null_result by val is not null;
	sorted = ORDER no_null_result BY val;
 	GENERATE Quantile(sorted.val);
};

has_null_result = foreach form_params_parse_result  generate (double)num.personal_year_income as (val:double);
personal_year_income_no_null_result_percentile = FOREACH ( GROUP has_null_result all) {
        no_null_result = filter has_null_result by val is not null;
        sorted = ORDER no_null_result BY val;
        GENERATE Quantile(sorted.val);
};

has_null_result = foreach form_params_parse_result  generate (double)num.age as (val:double);
age_no_null_result_percentile = FOREACH ( GROUP has_null_result all) {
        no_null_result = filter has_null_result by val is not null;
        sorted = ORDER no_null_result BY val;
        GENERATE Quantile(sorted.val);
};

has_null_result = foreach form_params_parse_result  generate (double)num.credit_query_time as (val:double);
credit_query_time_no_null_result_percentile = FOREACH ( GROUP has_null_result all) {
        no_null_result = filter has_null_result by val is not null;
        sorted = ORDER no_null_result BY val;
        GENERATE Quantile(sorted.val);
};

has_null_result = foreach form_params_parse_result  generate (double)num.credit_utilization as (val:double);
credit_utilization_no_null_result_percentile = FOREACH ( GROUP has_null_result all) {
        no_null_result = filter has_null_result by val is not null;
        sorted = ORDER no_null_result BY val;
        GENERATE Quantile(sorted.val);
};

dump credit_limit_no_null_result_percentile;
dump personal_year_income_no_null_result_percentile;
dump age_no_null_result_percentile;
dump credit_query_time_no_null_result_percentile;
dump credit_utilization_no_null_result_percentile;
credit_limit_no_null_result_percentile_output = foreach (foreach credit_limit_no_null_result_percentile generate Flatten($0)) generate $0 as p10, $1 as p20, $2 as p30, $3 as p40, $4 as p50, $5 as p60, $6 as p70, $7 as p80, $8 as p90, $9 as p95, $10 as p99, '$year$month$day' as date, 'creditLimit' as variable;
personal_year_income_no_null_result_percentile_output = foreach (foreach personal_year_income_no_null_result_percentile  generate Flatten($0)) generate $0 as p10, $1 as p20, $2 as p30, $3 as p40, $4 as p50, $5 as p60, $6 as p70, $7 as p80, $8 as p90, $9 as p95, $10 as p99, '$year$month$day' as date, 'personalYearIncome' as variable;
age_no_null_result_percentile_output = foreach (foreach age_no_null_result_percentile generate Flatten($0)) generate $0 as p10, $1 as p20, $2 as p30, $3 as p40, $4 as p50, $5 as p60, $6 as p70, $7 as p80, $8 as p90, $9 as p95, $10 as p99, '$year$month$day' as date, 'age' as variable;
credit_query_time_no_null_result_percentile_output = foreach (foreach credit_query_time_no_null_result_percentile generate Flatten($0)) generate $0 as p10, $1 as p20, $2 as p30, $3 as p40, $4 as p50, $5 as p60, $6 as p70, $7 as p80, $8 as p90, $9 as p95, $10 as p99, '$year$month$day' as date, 'creditQueryTimes' as variable;
credit_utilization_no_null_result_percentile_output = foreach (foreach credit_utilization_no_null_result_percentile generate Flatten($0)) generate $0 as p10, $1 as p20, $2 as p30, $3 as p40, $4 as p50, $5 as p60, $6 as p70, $7 as p80, $8 as p90, $9 as p95, $10 as p99, '$year$month$day' as date, 'creditUtilization' as variable;
--store credit_limit_no_null_result_percentile_output into 'model_monitor_xyb_credit_limit/line_chart_data' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');
--store personal_year_income_no_null_result_percentile_output into 'model_monitor_xyb_personal_year_income/line_chart_data' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');
--store age_no_null_result_percentile into 'model_monitor_xyb_age/line_chart_data' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');
--store credit_query_time_no_null_result_percentile into 'model_monitor_xyb_credit_querytimes/line_chart_data' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');
--store credit_utilization_no_null_result_percentilee_output into 'model_monitor_xyb_credit_utilization/line_chart_data' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');


     -- score percentile


score_result = foreach data generate (double)response_info.response_body  as (val:double);
score_percentile = FOREACH (GROUP score_result all) {
  sorted = ORDER score_result BY val;
  GENERATE Quantile(sorted.val);
}
score_percentile_output = foreach (foreach score_percentile generate Flatten($0))  generate $0 as p10, $1 as p20, $2 as p30, $3 as p40, $4 as p50, $5 as p60, $6 as p70, $7 as p80, $8 as p90, $9 as p95, $10 as p99, '$year$month$day' as date, 'score' as variable;
dump score_percentile_output;
--store score_percentile_output into 'model_monitor_xyb_score/line_chart_data' using org.elasticsearch.hadoop.pig.EsStorage('es.index.auto.create=true','es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');


