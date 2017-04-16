# -*- coding: utf-8 -*-
import re
import sys
import math
from decimal import Decimal
def regx(input, outs, type, add_missing):
	#type=0->number type=1->category
	output = []
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
				output.append(float(d[item]))
			else:
				output.append(d[item]) 
		else:
				
			if add_missing == 0:
					output.append(None)
			else:
				if type == 0:
					output.append(-1)
				else:
					output.append('missing')
	return  tuple(output)

def group(input, urange):

	for i in range(1, len(urange)):

		if input > urange[i - 1] and input <= urange[i]:
			return i - 1

	return -1

#number 组且有missing 组，mission组为0 
def number_group(input, urange):
	if input == -1:
		return 0
	for i in range(len(urange)):
		if input <= urange[i]:
			return i + 1
	#input 大于最大值
	return len(urange) + 1 	


#strong match
def string_group(input, urange):
	for i in range(len(urange)):
		if input  in urange[i]:
			return i

	#missing
	return 0
@outputSchema("group:int")
def group_score(input):
	g = group(input, score_group)
	return g

score_percent = [0.1022, 0.1, 0.1053, 0.0977, 0.1068, 0.0937, 0.0994, 0.0979, 0.1005, 0.0962]
age_percent = [0.0175, 0.0866, 0.0575, 0.5381, 0.1616, 0.1006, 0.0382]
personal_year_income_percent = [0.0028, 0.2924, 0.2866, 0.3783, 0.0399]
credit_query_time_percent = [0.3132, 0.1149, 0.1233, 0.311, 0.0392, 0.065, 0.0334]
credit_limit_percent = [0.1489, 0.0386, 0.2557, 0.3088, 0.0959, 0.1521]
credit_utilization_percent = [0.1518, 0.1142, 0.3324, 0.2006, 0.0967, 0.1042]

#从低到高
score_group = [449, 555, 570, 582, 592, 602, 611, 621, 633, 650, 736]

#mission <=28 <=30..... >54		
age_group = [ 28, 30, 45, 50, 54]
personal_year_income_group = [11.68, 18, 50]
credit_query_time_group = [0, 1, 5, 6, 9]
credit_limit_group = [6100, 48598, 156500, 232000]
credit_utilization_group = [0.249813, 0.6888235, 0.8948689, 0.9859697]



education_percent = [0, 0.0093, 0.3742, 0.5093, 0.1072]
personal_live_case_percent = [0, 0.1298, 0.3753, 0.0765, 0.1652, 0.0655, 0.1877]
personal_live_join_percent = [0, 0.8698, 0.1197, 0.0105]
client_gender_percent = [0.0175, 0.6832, 0.2994]

education_group = [['missing'],['1'], ['2'], ['3'], ['4']]
personal_live_case_group = [['missing'], ['1'], ['2'], ['3'], ['4'], ['5','8'], ['6','7']]
personal_live_join_group = [['missing'], ['1,', '2,', '1,2,'], ['4,'], ['3,', '2,4,', '1,4,', '1,2,4,']]
client_gender_group = [['missing'], ['1'], ['2']]
#@outputSchema("group:int")
def group_age(input):
        g = number_group(input, age_group)
        return g

def group_personal_year_income(input):
	return  number_group(input, personal_year_income_group)

def group_credit_query_time(input):
	return number_group(input, credit_query_time_group) 

def group_credit_limit(input):
        return number_group(input, credit_limit_group)


def group_credit_utilization(input):
	return number_group(input, credit_utilization_group)


def group_education(input):
	return string_group(input, education_group)

def group_personal_live_join(input):
	return string_group(input, personal_live_join_group)

def group_client_gender(input):
	return string_group(input, client_gender_group)

def group_personal_live_case(input):
	return string_group(input, personal_live_case_group)


def psi(input, percent_group):
	d = {}
	psi = 0
	for i in range(len(input)):

		d[input[i][0]] = input[i][1]
	for i in range(len(percent_group)):
		p = 0
		if d.has_key(i):
			p = d[i] 
			if p != 0 and percent_group[i] != 0:
				psi = psi + (p - percent_group[i]) * math.log(p / percent_group[i])
				
	return psi

@outputSchema("psi:double")
def psi_score(input):
	return  psi(input, score_percent)
	
@outputSchema("psi:double")
def psi_age(input):
	return psi(input, age_percent)

@outputSchema("psi:double")
def psi_personal_year_income(input):
        return psi(input, personal_year_income_percent)

@outputSchema("psi:double")
def psi_credit_query_time(input):
        return psi(input, credit_query_time_percent)

@outputSchema("psi:double")
def psi_credit_limit(input):
        return psi(input, credit_limit_percent)


@outputSchema("psi:double")
def psi_credit_utilization(input):
        return psi(input, credit_utilization_percent)


@outputSchema("psi:double")
def psi_education(input):
        return psi(input, education_percent)

@outputSchema("psi:double")
def psi_client_gender(input):
        return psi(input, client_gender_percent)

@outputSchema("psi:double")
def psi_personal_live_join(input):
        return psi(input, personal_live_join_percent)

@outputSchema("psi:double")
def psi_personal_live_case(input):
        return psi(input, personal_live_case_percent)

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

@outputSchema("num:(credit_query_time: double, personal_year_income: double, credit_limit: double, credit_utilization: double, age: double)")
def regx_number(input, add_missing):
	outs = ['creditQueryTimes', 'personalYearIncome', 'creditLimit', 'creditUtilization', 'age']
	return  regx(input, outs, 0, add_missing)

@outputSchema("category:(personalLiveJoin: chararray, clientGender: chararray, personalEducation: chararray, personalLiveCase: chararray)")
def regx_category(input, add_missing):
	outs = ['personalLiveJoin', 'clientGender', 'personalEducation', 'personalLiveCase']
	return  regx(input, outs, 1, add_missing)
