/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 99.9952380952381, "KoPercent": 0.004761904761904762};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.26580952380952383, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.11975, 500, 1500, "login Users"], "isController": false}, {"data": [0.251, 500, 1500, "Purchase Products Order"], "isController": false}, {"data": [0.1565, 500, 1500, "Mark wish"], "isController": false}, {"data": [0.3685, 500, 1500, "See your marked wishes"], "isController": false}, {"data": [0.0215, 500, 1500, "give feedback"], "isController": false}, {"data": [0.171, 500, 1500, "View History of Orders Bought"], "isController": false}, {"data": [0.37683333333333335, 500, 1500, "products by category"], "isController": false}, {"data": [0.29833333333333334, 500, 1500, "show all products"], "isController": false}, {"data": [0.2911666666666667, 500, 1500, "one product detail"], "isController": false}, {"data": [0.0575, 500, 1500, "Create Users"], "isController": false}, {"data": [0.4155, 500, 1500, "show feedbacks of other people for this prod"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 21000, 1, 0.004761904761904762, 4443.639333333348, 1, 39754, 3294.0, 9847.900000000001, 15750.850000000002, 24512.850000000024, 156.3011700259013, 14384.157588445065, 58.77552715593275], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["login Users", 2000, 0, 0.0, 4636.7045000000035, 117, 16555, 4544.5, 8743.9, 9467.0, 9918.95, 17.97155103471205, 13.88044327392238, 5.633660041154852], "isController": false}, {"data": ["Purchase Products Order", 1000, 0, 0.0, 2412.102000000004, 16, 16771, 1560.5, 4908.7, 6030.699999999998, 7669.6900000000005, 33.59199166918607, 28.669387093117003, 23.12654444430448], "isController": false}, {"data": ["Mark wish", 1000, 0, 0.0, 6580.654, 3, 39754, 4349.5, 17120.0, 22035.599999999995, 26810.12000000001, 13.54261182811717, 5.752964985577119, 5.898442261074472], "isController": false}, {"data": ["See your marked wishes", 1000, 0, 0.0, 4118.1910000000025, 2, 33779, 1424.0, 11861.699999999999, 18307.6, 25228.97, 14.441684478077523, 6.332058102507077, 5.993863186702097], "isController": false}, {"data": ["give feedback", 1000, 0, 0.0, 14669.997999999998, 12, 36904, 13639.5, 24597.4, 25482.999999999993, 30474.33000000001, 13.219644391565867, 5.835233657214621, 10.03736356913874], "isController": false}, {"data": ["View History of Orders Bought", 2000, 0, 0.0, 6374.601999999983, 2, 29231, 3059.5, 19285.200000000004, 21077.199999999997, 25474.73, 16.78852336542739, 9.431756357603941, 6.869522744252029], "isController": false}, {"data": ["products by category", 3000, 0, 0.0, 3561.414666666659, 1, 20983, 3237.0, 8201.1, 9974.599999999999, 17296.039999999914, 25.008336112037348, 281.01241266620127, 8.222141755585195], "isController": false}, {"data": ["show all products", 3000, 0, 0.0, 3584.8469999999916, 7, 20846, 3097.0, 7770.6, 8846.55, 10214.209999999961, 25.4612734031538, 77.75136907388863, 7.948359763549641], "isController": false}, {"data": ["one product detail", 3000, 0, 0.0, 4139.427000000007, 42, 29593, 4000.0, 8868.100000000004, 10639.699999999999, 13543.869999999997, 25.19272434121026, 8288.910786002503, 7.8153210602777925], "isController": false}, {"data": ["Create Users", 1000, 0, 0.0, 2865.161999999999, 213, 4225, 2992.0, 3580.9, 3648.7499999999995, 3955.5000000000005, 87.070091423596, 67.249146304963, 30.09130455485416], "isController": false}, {"data": ["show feedbacks of other people for this prod", 3000, 1, 0.03333333333333333, 2263.5466666666716, 8, 30587, 1693.0, 5595.200000000001, 5986.499999999998, 10745.609999999991, 25.086968156275088, 7501.56901346543, 7.978505172096601], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["400", 1, 100.0, 0.004761904761904762], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 21000, 1, "400", 1, "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["show feedbacks of other people for this prod", 3000, 1, "400", 1, "", "", "", "", "", "", "", ""], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
