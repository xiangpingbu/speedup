DEFINE psi(d,v,isn,f) RETURNS ret{

	psi_data = LOAD '$f' USING PigStorage('$') as (variable:chararray, grp:chararray, percent:chararray, type:chararray);
	variable_psi_data = filter psi_data by variable == '$v';
	variable_psi_data = limit variable_psi_data 1;
	form_params_parse_result_map_group = foreach $d generate pyudf.group_variable(val, $isn, variable_psi_data.grp);
	--dump form_params_parse_result_map_group;
	form_params_parse_result_map_group_count = FOREACH (GROUP form_params_parse_result_map_group ALL) GENERATE COUNT(form_params_parse_result_map_group) as sum;
	--dump form_params_parse_result_map_group_count;
	form_params_parse_result_group_map_percent = foreach (GROUP form_params_parse_result_map_group by variable_grp.g) generate group as g, COUNT(form_params_parse_result_map_group)/(double)form_params_parse_result_map_group_count.sum as percent;
	--dump form_params_parse_result_group_map_percent;
	$ret = foreach (GROUP form_params_parse_result_group_map_percent all) generate pyudf.psi_variable(form_params_parse_result_group_map_percent, variable_psi_data.percent);
	 
	--dump form_params_parse_result_psi;
};

