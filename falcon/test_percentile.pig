IMPORT 'utils/include.pig';
IMPORT 'utils/percentile.pig';

test_data = LOAD '/users/techang/percentile_test.dat' USING PigStorage() as (val:double);
dump test_data;
data_percentile = quantile_result(test_data);
dump data_percentile;
