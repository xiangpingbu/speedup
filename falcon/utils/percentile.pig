DEFINE Quantile datafu.pig.stats.Quantile('0.1', '0.2', '0.3', '0.4', '0.5', '0.6', '0.7', '0.8', '0.9', '0.95', '0.99');
--Schema 一定要有(val:double)
DEFINE quantile_result(data) RETURNS ret{

  q = FOREACH ( GROUP $data all) {
    no_null_data = filter $data by val is not null;
    sorted = ORDER no_null_data BY val;
    GENERATE Quantile(sorted.val);
  };
  $ret = foreach q generate Flatten($0); 

};

DEFINE output_es(a,v,date) RETURNS ret{
  
  p1 = foreach $a  generate $0 as value, '10' as percent, '$date' as date, '$v' as variable;
  p2 = foreach $a  generate $1 as value, '20' as percent, '$date' as date, '$v' as variable;
  p3 = foreach $a  generate $2 as value, '30' as percent, '$date' as date, '$v' as variable;
  p4 = foreach $a  generate $3 as value, '40' as percent, '$date' as date, '$v' as variable;
  p5 = foreach $a  generate $4 as value, '50' as percent, '$date' as date, '$v' as variable;
  p6 = foreach $a  generate $5 as value, '60' as percent, '$date' as date, '$v' as variable;
  p7 = foreach $a  generate $6 as value, '70' as percent, '$date' as date, '$v' as variable;
  p8 = foreach $a  generate $7 as value, '80' as percent, '$date' as date, '$v' as variable;
  p9 = foreach $a  generate $8 as value, '90' as percent, '$date' as date, '$v' as variable;
  p10 = foreach $a  generate $9 as value, '95' as percent, '$date' as date, '$v' as variable;
  p11 = foreach $a  generate $10 as value, '99' as percent, '$date' as date, '$v' as variable;
  $ret = UNION p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11;
};



