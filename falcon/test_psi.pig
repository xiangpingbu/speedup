IMPORT 'utils/include.pig';
IMPORT 'utils/psi.pig';

%declare psi_base_file '/users/techang/psi_test.dat';
test_data = LOAD '/users/techang/psi_test.dat' USING PigStorage() as (val:chararray);
dump test_data;
v = psi(test_data, '$variable', 1, '$psi_base_file');

/*
psi_data = LOAD '/users/techang/psi.dat' USING PigStorage('$') as (variable:chararray, grp:chararray, percent:chararray, type:chararray);

variable_psi_data = filter psi_data by variable == '$variable';
variable_psi_data = limit variable_psi_data 1;
dump variable_psi_data;
form_params_parse_result_map_group = foreach test_data generate pyudf.group_variable(val, 1, variable_psi_data.grp);
dump form_params_parse_result_map_group;
form_params_parse_result_map_group_count = FOREACH (GROUP form_params_parse_result_map_group ALL) GENERATE COUNT(form_params_parse_result_map_group) as sum;
dump form_params_parse_result_map_group_count;
form_params_parse_result_group_map_percent = foreach (GROUP form_params_parse_result_map_group by variable_grp.g) generate group as g, COUNT(form_params_parse_result_map_group)/(double)form_params_parse_result_map_group_count.sum as percent;
dump form_params_parse_result_group_map_percent;
psi = foreach (GROUP form_params_parse_result_group_map_percent all) generate pyudf.psi_variable(form_params_parse_result_group_map_percent, variable_psi_data.percent);
	*/ 

dump v;

