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

    var data = {"OkPercent": 85.71428571428571, "KoPercent": 14.285714285714286};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.8043095238095238, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.421375, 500, 1500, "login Users"], "isController": false}, {"data": [0.0, 500, 1500, "Purchase Products Order"], "isController": false}, {"data": [0.876, 500, 1500, "Mark wish"], "isController": false}, {"data": [0.87925, 500, 1500, "See your marked wishes"], "isController": false}, {"data": [0.8655, 500, 1500, "give feedback"], "isController": false}, {"data": [0.439, 500, 1500, "View History of Orders Bought"], "isController": false}, {"data": [1.0, 500, 1500, "products by category"], "isController": false}, {"data": [0.95925, 500, 1500, "show all products"], "isController": false}, {"data": [0.9559166666666666, 500, 1500, "one product detail"], "isController": false}, {"data": [1.0, 500, 1500, "Create Users"], "isController": false}, {"data": [0.9345, 500, 1500, "show feedbacks of other people for this prod"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 42000, 6000, 14.285714285714286, 130.03242857142837, 0, 7621, 3.0, 27.0, 35.0, 52.0, 101.02467888584212, 8255.439127087393, 32.47485376226728], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["login Users", 4000, 2000, 50.0, 200.5349999999999, 1, 3697, 63.0, 724.6000000000004, 1223.0, 1912.6899999999932, 12.645382380556459, 7.396394060542929, 4.646313594418328], "isController": false}, {"data": ["Purchase Products Order", 2000, 2000, 100.0, 0.6590000000000001, 0, 8, 1.0, 1.0, 1.0, 1.0, 20.05676063259023, 7.442938516000281, 9.237088225302607], "isController": false}, {"data": ["Mark wish", 2000, 0, 0.0, 271.81200000000007, 1, 3990, 3.0, 1053.6000000000004, 1390.8999999999996, 2250.210000000001, 17.208890112632186, 7.310417186518555, 7.495278310775347], "isController": false}, {"data": ["See your marked wishes", 2000, 0, 0.0, 265.6064999999997, 1, 7621, 3.0, 1030.7000000000003, 1352.8499999999995, 1971.8400000000001, 17.21007477777491, 8.620020574106583, 7.142853301322594], "isController": false}, {"data": ["give feedback", 2000, 0, 0.0, 293.9069999999998, 3, 5104, 8.0, 1118.0, 1424.699999999999, 2066.010000000001, 17.327569028703117, 7.648497266575985, 13.166033510977014], "isController": false}, {"data": ["View History of Orders Bought", 4000, 2000, 50.0, 133.4432500000002, 0, 4810, 1.0, 527.0, 1050.7999999999993, 1776.0, 12.651381689022712, 5.022252594323958, 3.7373466415325884], "isController": false}, {"data": ["products by category", 6000, 0, 0.0, 1.3714999999999995, 0, 57, 1.0, 2.0, 2.0, 6.0, 18.973891924711594, 136.74543821191625, 4.799060555176077], "isController": false}, {"data": ["show all products", 6000, 0, 0.0, 93.19833333333324, 1, 6109, 5.0, 130.8000000000011, 776.7999999999993, 1625.0, 18.97395192633047, 58.122822936661784, 4.484078482589818], "isController": false}, {"data": ["one product detail", 6000, 0, 0.0, 118.86649999999975, 2, 4075, 28.0, 148.60000000000218, 888.7999999999993, 1689.7999999999956, 18.973171934884075, 5897.991543993252, 4.446837172238455], "isController": false}, {"data": ["Create Users", 2000, 0, 0.0, 88.1455, 65, 364, 83.0, 105.0, 116.0, 236.92000000000007, 20.1684061917007, 15.577198513840568, 9.20563659683356], "isController": false}, {"data": ["show feedbacks of other people for this prod", 6000, 0, 0.0, 167.42850000000016, 2, 4672, 6.0, 469.8000000000011, 1247.699999999999, 2091.809999999996, 18.97689247060163, 4732.800473888587, 4.595966145223833], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["400", 2000, 33.333333333333336, 4.761904761904762], "isController": false}, {"data": ["403", 4000, 66.66666666666667, 9.523809523809524], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 42000, 6000, "403", 4000, "400", 2000, "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["login Users", 4000, 2000, "400", 2000, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["Purchase Products Order", 2000, 2000, "403", 2000, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["View History of Orders Bought", 4000, 2000, "403", 2000, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
