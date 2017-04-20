IMPORT 'utils/include.pig';
IMPORT 'utils/psi.pig';
%declare psi_base_file '/users/techang/psi_test.dat';
 /*  
    执行命令：pig -p year=2017 -p month=04 -p day=11 -p variable=age -p isnumber=1 modelxyb_psi_variable.pig
    isnumber=1 =>number variable; isnumber=0 =>category
    
    "form_params":"creditQueryTimes:{2};clientGender:{2};personalEducation:{4};personalLiveJoin:{2,};
    personalYearIncome:{7.00};creditLimit:{0.00};creditUtilization:{0.00000};age:{47};

*/

data = LOAD '/topics/kafka_lookup_event_v1/year=$year/month=$month/day=$day/api_path=model_xyb' USING AvroStorage();
data = filter data by response_info.response_code == 200;
describe data;
form_params_result = foreach data  generate request_info.form_params, response_info.response_body as (val:chararray);
form_params_parse_result = foreach form_params_result generate ( CASE WHEN '$variable' != 'score' THEN pyudf.regx_form_data(form_params, '$variable') 
																	  WHEN '$variable' == 'score' THEN val 
																 END);
dump form_params_parse_result;
form_params_parse_result_psi = psi(form_params_parse_result, '$variable', $isnumber, '$psi_base_file');
form_params_parse_result_psi = foreach form_params_parse_result_psi generate psi as PSI, '$year-$month-$day' as date ;
dump form_params_parse_result_psi;




