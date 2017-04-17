register '/home/techang/avro-mapred-1.7.5.jar';
REGISTER '/home/techang/piggybank-0.16.0.jar';
register '/home/techang/es-hadoop.jar';
register '/home/techang/datafu-pig-incubating-1.3.2.jar';
register 'xybudf.py' using  jython  as pyudf;
DEFINE Quantile datafu.pig.stats.Quantile('0.1', '0.2', '0.3', '0.4', '0.5', '0.6', '0.7', '0.8', '0.9', '0.95', '0.99');
data = LOAD '/topics/kafka_lookup_event_v1/year=$year/month=$month/day=$day/api_path=model_xyb' USING AvroStorage();
data = filter data by response_info.response_code == 200;
describe data;

--dump data;


  --- score psi


score_result = foreach data generate (double)response_info.response_body  as (score:double);
score_result_count = FOREACH (GROUP score_result ALL) GENERATE COUNT(score_result) as sum;
score_map_group = foreach score_result GENERATE score, pyudf.group_score(score) as g;
score_group_map_percent = foreach (GROUP score_map_group by g) generate group as g, COUNT(score_map_group)/(double)score_result_count.sum as percent;
score_psi_result = foreach (GROUP score_group_map_percent all) generate pyudf.psi_score(score_group_map_percent);
score_psi_result = foreach score_psi_result generate psi as PSI, '$year$month$day' as date ;  
dump score_psi_result;
--store score_psi_result into 'model_monitor_xyb_psi/score' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');


------category psi


form_params_result = foreach data  generate request_info.form_params;
form_params_parse_category_result = foreach form_params_result generate pyudf.regx_category(form_params, 1);
--分组
category_result = foreach form_params_parse_category_result generate pyudf.group_category(category.personalEducation, category.personalLiveJoin, category.clientGender, category.personalLiveCase);
category_result_count = FOREACH (GROUP category_result ALL) GENERATE COUNT(category_result) as sum;

--personal_education psi 
personal_education_map_group = foreach category_result GENERATE category.personal_education.value as personal_education, category.personal_education.group as g;
personal_education_group_map_percent = foreach (GROUP personal_education_map_group by g) generate group as g, COUNT(personal_education_map_group)/(double)category_result_count.sum as percent;
personal_education_psi_result = foreach (GROUP personal_education_group_map_percent all) generate pyudf.psi_education(personal_education_group_map_percent);
personal_education_psi_result = foreach personal_education_psi_result generate psi as PSI, '$year$month$day' as date ;
dump personal_education_psi_result;

--store personal_education_psi_result into 'model_monitor_xyb_psi/personal_education' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');


--personal_live_join psi


personal_live_join_map_group = foreach category_result GENERATE category.personal_live_join.value as personal_live_join, category.personal_live_join.group as g;
--dump personal_live_join_map_group;
describe personal_live_join_map_group
personal_live_join_group_map_percent = foreach (GROUP personal_live_join_map_group by g) generate group as g, COUNT(personal_live_join_map_group)/(double)category_result_count.sum as percent;
personal_live_join_psi_result = foreach (GROUP personal_live_join_group_map_percent all) generate pyudf.psi_personal_live_join(personal_live_join_group_map_percent);
personal_live_join_psi_result = foreach personal_live_join_psi_result generate psi as PSI, '$year$month$day' as date ;
dump personal_live_join_psi_result;
--store personal_live_join_psi_result into 'model_monitor_xyb_psi/personal_live_join' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');

---client_gender psi

client_gender_map_group = foreach category_result GENERATE category.client_gender.value as client_gender, category.client_gender.group as g;
client_gender_group_map_percent = foreach (GROUP client_gender_map_group by g) generate group as g, COUNT(client_gender_map_group)/(double)category_result_count.sum as percent;
client_gender_psi_result = foreach (GROUP client_gender_group_map_percent all) generate pyudf.psi_client_gender(client_gender_group_map_percent);
client_gender_psi_result = foreach client_gender_psi_result generate psi as PSI, '$year$month$day' as date ;
dump client_gender_psi_result;

--store client_gender_psi_result into 'model_monitor_xyb_psi/client_gender' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');

--personal_live_case psi

personal_live_case_map_group = foreach category_result GENERATE category.personal_live_case.value as personal_live_case, category.personal_live_case.group as g;
personal_live_case_group_map_percent = foreach (GROUP personal_live_case_map_group by g) generate group as g, COUNT(personal_live_case_map_group)/(double)category_result_count.sum as percent;
personal_live_case_psi_result = foreach (GROUP personal_live_case_group_map_percent all) generate pyudf.psi_personal_live_case(personal_live_case_group_map_percent);
personal_live_case_psi_result = foreach personal_live_case_psi_result generate psi as PSI, '$year$month$day' as date ;
dump personal_live_case_psi_result;
--store personal_live_case_psi_result into 'model_monitor_xyb_psi/personal_live_case' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');



----number psi

form_params_parse_number_result = foreach form_params_result generate pyudf.regx_number(form_params, 1);
--分组
number_result = foreach form_params_parse_number_result generate pyudf.group_number(num.age, num.personal_year_income, num.credit_query_time, num.credit_limit, num.credit_utilization);
number_result_count = FOREACH (GROUP number_result ALL) GENERATE COUNT(number_result) as sum;


----age psi
age_map_group = foreach number_result GENERATE number.age.value as age, number.age.group as g;
age_group_map_percent = foreach (GROUP age_map_group by g) generate group as g, COUNT(age_map_group)/(double)number_result_count.sum as percent;
age_psi_result = foreach (GROUP age_group_map_percent all) generate pyudf.psi_age(age_group_map_percent);
age_psi_result = foreach age_psi_result generate psi as PSI, '$year$month$day' as date ;
dump age_psi_result;


---- personalYearIncome psi


personal_year_income_map_group = foreach number_result GENERATE number.personal_year_income.value as personal_year_income, number.age.group as g;
personal_year_income_group_map_percent = foreach (GROUP personal_year_income_map_group by g) generate group as g, COUNT(personal_year_income_map_group)/(double)number_result_count.sum as percent;
personal_year_income_psi_result = foreach (GROUP personal_year_income_group_map_percent all) generate pyudf.psi_personal_year_income(personal_year_income_group_map_percent);
personal_year_income_psi_result = foreach personal_year_income_psi_result generate psi as PSI, '$year$month$day' as date ;
dump personal_year_income_psi_result;
--store psi_result into 'model_monitor_xyb_psi/score' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');



---- creditquerytime psi



credit_query_time_map_group = foreach number_result GENERATE number.credit_query_time.value as credit_query_time, number.credit_query_time.group as g;
credit_query_time_group_map_percent = foreach (GROUP credit_query_time_map_group by g) generate group as g, COUNT(credit_query_time_map_group)/(double)number_result_count.sum as percent;
credit_query_time_psi_result = foreach (GROUP credit_query_time_group_map_percent all) generate pyudf.psi_credit_query_time(credit_query_time_group_map_percent);
credit_query_time_psi_result = foreach credit_query_time_psi_result generate psi as PSI, '$year$month$day' as date ;
dump credit_query_time_psi_result;
--store psi_result into 'model_monitor_xyb_psi/score' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');





------ credit_limit psi



credit_limit_map_group = foreach number_result GENERATE number.credit_limit.value as credit_limit, number.credit_limit.group as g;
credit_limit_group_map_percent = foreach (GROUP credit_limit_map_group by g) generate group as g, COUNT(credit_limit_map_group)/(double)number_result_count.sum as percent;
credit_limit_psi_result = foreach (GROUP credit_limit_group_map_percent all) generate pyudf.psi_credit_limit(credit_limit_group_map_percent);
credit_limit_psi_result = foreach credit_limit_psi_result generate psi as PSI, '$year$month$day' as date ;
dump credit_limit_psi_result;
--store psi_result into 'model_monitor_xyb_psi/score' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');




--- credit_utilization


credit_utilization_map_group = foreach number_result GENERATE number.credit_utilization.value as credit_utilization, number.credit_utilization.group as g;
credit_utilization_group_map_percent = foreach (GROUP credit_utilization_map_group by g) generate group as g, COUNT(credit_utilization_map_group)/(double)number_result_count.sum as percent;
credit_utilization_psi_result = foreach (GROUP credit_utilization_group_map_percent all) generate pyudf.psi_credit_utilization(credit_utilization_group_map_percent);
credit_utilization_psi_result = foreach credit_utilization_psi_result generate psi as PSI, '$year$month$day' as date ;
dump credit_utilization_psi_result;

--store psi_result into 'model_monitor_xyb_psi/score' using org.elasticsearch.hadoop.pig.EsStorage('es.nodes.wan.only=true', 'es.nodes:10.10.10.107', 'es.port=9200', 'es.http.timeout=5m');





