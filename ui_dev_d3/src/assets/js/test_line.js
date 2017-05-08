var data_set = [{
    "p60": 5000,
    "p70": 95010,
    "p99": 95010,
    "p90": 95010,
    "p80": 95010,
    "p95": 95010,
    "p10": 0,
    "variable": "creditLimit",
    "p20": 0,
    "date": "20170409",
    "p30": 0,
    "p50": 5000,
    "p40": 5000
  },
  {
    "p60": 80000,
    "p70": 114945,
    "p99": 1205910,
    "p90": 308616,
    "p80": 186911,
    "p95": 539446,
    "p10": 100,
    "variable": "creditLimit",
    "p20": 12000,
    "date": "20170410",
    "p30": 23000,
    "p50": 51572,
    "p40": 37273
  },
  {
    "p60": 105488,
    "p70": 148710,
    "p99": 953573,
    "p90": 359536,
    "p80": 218004,
    "p95": 711697,
    "p10": 0,
    "variable": "creditLimit",
    "p20": 12087,
    "date": "20170411",
    "p30": 28000,
    "p50": 74350,
    "p40": 48860
  },
  {
    "p60": 5000,
    "p70": 95010,
    "p99": 95010,
    "p90": 95010,
    "p80": 95010,
    "p95": 95010,
    "p10": 0,
    "variable": "creditLimit",
    "p20": 0,
    "date": "20170412",
    "p30": 0,
    "p50": 5000,
    "p40": 5000
  },
  {
    "p60": 5000,
    "p70": 95010,
    "p99": 95010,
    "p90": 95010,
    "p80": 95010,
    "p95": 95010,
    "p10": 0,
    "variable": "creditLimit",
    "p20": 0,
    "date": "20170413",
    "p30": 0,
    "p50": 5000,
    "p40": 5000
  }
]

var chart1 = c3.generate({
  bindto: '#chart1',
  data: {
    // columns: [
    //   generateData(100)
    // ],
    json: data_set,
    keys: {
      x: 'date',
      xFormat: '%Y%m%d',
      value: ['p50', 'p80', 'p10', 'p20', 'p30', 'p40', 'p60', 'p70', 'p90', 'p95', 'p99']
    },
    xFormat: '%Y%m%d'
  },
  axis: {
    x: {
      // default: [30, 60]
      type: 'timeseries',
      tick: {
        format: '%Y-%m-%d'
      }
    }
  },
  zoom: {
    enabled: true,
    onzoomstart: function(event) {
      console.log("onzoomstart", event);
    },
    onzoomend: function(domain) {
      console.log("onzoomend", domain);
    },
  },
  subchart: {
    show: true
  }
});