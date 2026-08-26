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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.5105, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.1495, 500, 1500, "login Users"], "isController": false}, {"data": [0.0, 500, 1500, "Purchase Products Order"], "isController": false}, {"data": [0.2885, 500, 1500, "Mark wish"], "isController": false}, {"data": [0.447, 500, 1500, "See your marked wishes"], "isController": false}, {"data": [0.122, 500, 1500, "give feedback"], "isController": false}, {"data": [0.06225, 500, 1500, "View History of Orders Bought"], "isController": false}, {"data": [0.7615, 500, 1500, "products by category"], "isController": false}, {"data": [0.7593333333333333, 500, 1500, "show all products"], "isController": false}, {"data": [0.742, 500, 1500, "one product detail"], "isController": false}, {"data": [0.07, 500, 1500, "Create Users"], "isController": false}, {"data": [0.8603333333333333, 500, 1500, "show feedbacks of other people for this prod"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 21000, 3000, 14.285714285714286, 898.0435714285652, 0, 9984, 30.0, 2711.9000000000015, 3168.9500000000007, 4527.0, 362.46893123446563, 14434.634769400718, 112.73908048859948], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["login Users", 2000, 1000, 50.0, 882.0154999999993, 2, 4060, 190.0, 2712.8, 3045.95, 3248.98, 42.11767678894832, 24.634995215695152, 13.202904540285559], "isController": false}, {"data": ["Purchase Products Order", 1000, 1000, 100.0, 1.5199999999999998, 0, 94, 1.0, 2.0, 3.0, 17.970000000000027, 98.47365829640572, 36.54295913343181, 45.386259078040375], "isController": false}, {"data": ["Mark wish", 1000, 0, 0.0, 2020.1029999999962, 2, 7714, 1916.5, 3980.2, 4495.0, 6105.130000000002, 39.444619753865574, 16.75625936809719, 17.17998086935942], "isController": false}, {"data": ["See your marked wishes", 1000, 0, 0.0, 1607.262000000001, 2, 6440, 1347.5, 3554.8, 4309.549999999999, 4837.99, 40.778045100517886, 20.423548492741507, 16.924481609101658], "isController": false}, {"data": ["give feedback", 1000, 0, 0.0, 2566.791999999999, 10, 9984, 2489.0, 4340.9, 4544.0, 6325.160000000001, 38.56685564425932, 17.023651124223843, 29.285161824115857], "isController": false}, {"data": ["View History of Orders Bought", 2000, 1000, 50.0, 1288.9415000000054, 0, 9123, 8.0, 3577.2000000000007, 4369.499999999998, 5256.4400000000005, 43.20214282628419, 17.150069393441917, 12.762351762647429], "isController": false}, {"data": ["products by category", 3000, 0, 0.0, 501.4273333333341, 0, 3636, 2.0, 1978.0, 2182.3499999999976, 2512.9699999999993, 64.17524119194815, 462.5129687466575, 16.23182369991657], "isController": false}, {"data": ["show all products", 3000, 0, 0.0, 586.8610000000024, 2, 6557, 7.0, 2309.9, 2825.8999999999996, 3159.959999999999, 64.00819304871024, 196.05994984024622, 15.126936247839723], "isController": false}, {"data": ["one product detail", 3000, 0, 0.0, 643.5233333333327, 2, 5569, 31.0, 2371.9, 2787.7999999999993, 3249.6899999999932, 64.13956769931372, 9017.190260172802, 15.032711179526649], "isController": false}, {"data": ["Create Users", 1000, 0, 0.0, 2260.7399999999984, 258, 3492, 2324.0, 2936.0, 3017.8999999999996, 3395.0, 97.7803852547179, 75.52131077662071, 33.79276745991004], "isController": false}, {"data": ["show feedbacks of other people for this prod", 3000, 0, 0.0, 288.38300000000004, 1, 7351, 5.0, 1014.6000000000004, 1338.9499999999998, 2588.8299999999963, 64.58001463813665, 8163.763333923074, 15.64047229517372], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["400", 1000, 33.333333333333336, 4.761904761904762], "isController": false}, {"data": ["403", 2000, 66.66666666666667, 9.523809523809524], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 21000, 3000, "403", 2000, "400", 1000, "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["login Users", 2000, 1000, "400", 1000, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["Purchase Products Order", 1000, 1000, "403", 1000, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["View History of Orders Bought", 2000, 1000, "403", 1000, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
