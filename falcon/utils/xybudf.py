# -*- coding: utf-8 -*-
import re
import sys
import math
from decimal import Decimal

def regx(input, outs, type, add_missing):
	#type=0->number type=1->category
	output = None
	arr = input.split(';')
	d = {}
	for i, item in enumerate(arr):
		a = item.split(':')
		
		if len(a) == 2:
			match = re.search('{(.+)}', a[1])
			if match:
				s = match.group(0)[1:-1]
				if s != None and s != '':
					d[a[0]] = s
				
	for i, item in enumerate(outs):
		if d.has_key(item):
			if type == 0:
				output = float(d[item])
			else:
				output = d[item] 
		else:
				
			if add_missing == 0:
					output =None
			else:
				if type == 0:
					output = -1
				else:
					output = 'missing'
	return  output


def group(input, urange):

	for i in range(1, len(urange)):

		if input > urange[i - 1] and input <= urange[i]:
			return i - 1

	return -1

#number 组且有missing 组，mission组为0 
def number_group(input, urange):
	if input == 'missing':
		return 0
	else:
		input = int(input)

	for i in range(1, len(urange)):
		if input <= int(urange[i]):
			return i 
	#input 大于最大值
	return len(urange)  	


#strong match
def string_group(input, urange):
	for i in range(len(urange)):
		t = urange[i].split('|')
		if input in t:
			return i
	#missing
	return 0


def psi(input, percent_group):
	d = {}
	psi = 0
	for i in range(len(input)):

		d[input[i][0]] = float(input[i][1])
	for i in range(len(percent_group)):
		p = 0
		if d.has_key(i):
			p = d[i] 
			base_p = float(percent_group[i])
			if p != 0 and base_p != 0:
				psi = psi + (p - base_p) * math.log(p / base_p)
				
	return psi


@outputSchema("psi:double")
def psi_variable(input, percent):
	p = percent.split(';')
	return psi(input, p)

@outputSchema("number:(age:(value:int, group:int), personal_year_income:(value:double, group:int), credit_query_time:(value:int, group:int), credit_limit:(value:double, group:int), credit_utilization:(value:double, group:int) )")
def group_number(age, personal_year_income, credit_query_time, credit_limit, credit_utilization):
	age_t = (age, group_age(age))
	personal_year_income_t = (personal_year_income, group_personal_year_income(personal_year_income))
	credit_query_time_t = (credit_query_time, group_credit_query_time(credit_query_time))
	credit_limit_t = (credit_limit, group_credit_limit(credit_limit))
	credit_utilization_t = (credit_utilization, group_credit_utilization(credit_utilization))
	return (age_t, personal_year_income_t, credit_query_time_t, credit_limit_t, credit_utilization_t)	

@outputSchema("category:(personal_education:(value:chararray, group:int), personal_live_join:(value:chararray, group:int), client_gender:(value:chararray, group:int), personal_live_case:(value:chararray, group:int) )")
def group_category(personal_education, personal_live_join, client_gender, personal_live_case):
	personal_education_t = (personal_education, group_education(personal_education))
	personal_live_join_t = (personal_live_join, group_personal_live_join(personal_live_join))
	client_gender_t = (client_gender, group_client_gender(client_gender))
	personal_live_case_t = (personal_live_case, group_personal_live_case(personal_live_case))
	return (personal_education_t, personal_live_join_t, client_gender_t, personal_live_case_t)	



@outputSchema("variable_grp:(value:chararray, g:int)")
def group_variable(val, isnumber, input_grp):

	g = input_grp.split(';')
	if isnumber == 1:
		return (str(val), number_group(val, g))
	else:
		return (str(val), string_group(val, g))

@outputSchema("val: double")
def regx_number(input, add_missing, variable):
	outs = [variable]
	return  regx(input, outs, 0, add_missing)

@outputSchema("val: chararray")
def regx_category(input, add_missing, variable):
	outs = [variable]
	return  regx(input, outs, 1, add_missing)

@outputSchema("val: chararray")
def regx_form_data(input, variable):
	outs = [variable]
	return  regx(input, outs, 1, 1)




