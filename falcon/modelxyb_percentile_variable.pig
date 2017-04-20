IMPORT 'utils/include.pig';
IMPORT 'utils/percentile.pig';

/*
    
    执行命令：pig -p year=2017 -p month=04 -p day=11 -p variable=age  modelxyb_percentile_variable.pig
    variable为request的form_params里的是number的参数名,creditQueryTimes, personalYearIncome, creditLimit, age, creditUtilization
    
    "form_params":"creditQueryTimes:{2};clientGender:{2};personalEducation:{4};personalLiveJoin:{2,};
    personalYearIncome:{7.00};creditLimit:{0.00};creditUtilization:{0.00000};age:{47};

    只能对数字参数做pencentile
*/
data = LOAD '/topics/kafka_lookup_event_v1/year=$year/month=$month/day=$day/api_path=model_xyb' USING AvroStorage();
data = filter data by response_info.response_code == 200;
form_params_result = foreach data  generate request_info.form_params, (double)response_info.response_body as (val:double);
dump form_params_result;
form_params_parse_result = foreach form_params_result generate ( CASE WHEN '$variable' != 'score' THEN pyudf.regx_number(form_params, 0, '$variable') 
																	  WHEN '$variable' == 'score' THEN val 
																 END);
describe form_params_parse_result;
has_null_result = foreach form_params_parse_result  generate (double)val;
no_null_result_percentile = quantile_result(has_null_result);
dump no_null_result_percentile;
no_null_result_percentile_es = output_es(no_null_result_percentile, '$variable', '$year-$month-$day');
dump no_null_result_percentile_es;
describe no_null_result_percentile_es;


