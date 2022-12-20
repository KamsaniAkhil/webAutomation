var pageSplitText = "page " + page + " of " + Math.ceil(data.length / 50),
from = 0,
end = 49,
pass = 0,
fail = 0,
skip = 0,
Exception1 = 0,
Exception2 = 0,
Exception3 = 0,
Exception4 = 0,
methods = [],
seconds = [],
totaltime = 0,
page = 1,
timeout;

function LoadData() {
	var ele = document.getElementById("results-id"), tab = ele.insertRow(1);
	tab.setAttribute("style", "background-color: #8cf28e");
	
	var a = tab.insertCell(0),out = tab.insertCell(1);
	
	a.innerHTML = "PASSED",out.innerHTML = pass;
	
	tab = ele.insertRow(2);
	tab.setAttribute("style", "background-color: #f76d6b");
	
	a = tab.insertCell(0),out = tab.insertCell(1);
	
	a.innerHTML = "FAILED",out.innerHTML = fail;
	
	tab = ele.insertRow(3);
	tab.setAttribute("style", "background-color: #f9f37f");
	
	a = tab.insertCell(0),out = tab.insertCell(1);
	
	a.innerHTML = "SKIPPED",out.innerHTML = skip;
	
	tab = ele.insertRow(4);
	tab.setAttribute("style", "background-color: #87cefa");
	
	a = tab.insertCell(0),out = tab.insertCell(1);
	a.innerHTML = "TOTAL",out.innerHTML = pass + fail + skip;
	
	var time = parseTime(totaltime);
	
	document.getElementById("execution-time-id").innerHTML = time
}

function pagesplit(input) {
    "next" === input ?(from = 50 * page, end = 50 * (page + 1) - 1, document.getElementById("back-id").disabled = !1, page++) : (end = 50 * --page - 1, from = 50 * (page - 1), document.getElementById("forward-id").disabled = !1), end >= data.length && (end = data.length, from = 50 * (page - 1), document.getElementById("forward-id").disabled = !0),from <= 0 && (from = 0, end = 49, document.getElementById("back-id").disabled = !0), lineGraph(from, end), pageSplitText = "page " + page + " of " + Math.ceil(methods.length / 50), document.getElementById("page-button-id").value = pageSplitText
} 

function lineGraph(f, e){
	var tc = document.getElementById("time-chart-id"),c = document.getElementById("etime-chart-id");
		
		c.parentElement.removeChild(c);
		
	var ce = document.createElement("canvas");
	ce.id = "etime-chart-id",tc.appendChild(ce);
	
	var c = document.getElementById("etime-chart-id"),
	
	labs = {

            labels: methods.slice(f, e),
            datasets: [{
                    label: "Execution Time (S) / Test",
                    fill: !0,
                    lineTension: .1,
                    backgroundColor: "rgba(75,192,192,0.4)",
                    borderColor: "#42a1f4",
                    borderCapStyle: "butt",
                    borderDash: [],
                    borderDashOffset: 0,
                    borderJoinStyle: "miter",
                    pointBorderColor: "rgba(75,192,192,1)",
                    pointBackgroundColor: "#fff",
                    pointBorderWidth: 1,
                    pointHoverRadius: 5,
                    pointHoverBackgroundColor: "rgba(75,192,192,1)",
                    pointHoverBorderColor: "rgba(220,220,220,1)",
                    pointHoverBorderWidth: 2,
                    pointRadius: 3,
                    pointHitRadius: 5,
                    data: seconds.slice(f, e),
                    borderWidth: 1
                }

            ]
        }

    ;

    Chart.Line(c, {
        data: labs,
        options: {
            showLines: !0,
            scales: {
                xAxes: [{
                        ticks: {
                            display: !1
                        }
                    }

                ],
                yAxes: [{
                        ticks: {
                            display: !0
                        }
                    }

                ]
            }
        }
    })
}

function search(){
	var e, res;
	e = document.getElementById("search-input-id").value.toUpperCase();
	var res = document.getElementById("results-table-id");
		
	for(var i = 1, n = res.rows.length; i < n - 1; i++){
		var s = !1;
		
		if(e) {
			for(var j = 0, c = res.rows[i].cells.length; j < c; j++)
				if(res.rows[i].cells[j].cjildren[0].innerHTML.toUpperCase().indexOf(e) > -1){
					s = !0;
					break;
				}
			res.rows[i].style.display = s ? "" : "none";
		}else res.rows[i].style.display = "";
	}
}

function toggle(className) {
    var name = document.getElementsByClassName(className);
    for(i=0;i<name.length;i++){
    if (name[i].style.display === "none") {
        name[i].style.display = "";
    } else {
        name[i].style.display = "none";
    }
}
}

function continuousRefresh(){
    var ele = document.getElementById("refresh-id");
    document.getElementById("text");
    1 == ele.checked ? timeout = setTimeout("location.reload(true);", 10e3) : clearTimeout(timeout);
} window.addEventListener("load", function () {
    addDataFromJSON(), updateStatus(), lineGraph(0, 49), document.getElementById("page-button-id").value = pageSplitText, data.length < 51 && (document.getElementById("forward-id").disabled = !0), continuousRefresh()
	document.getElementById('body-id')
});

function addDataFromJSON(){
	var ele, tab, a = document.getElementById("all-rows-id"),
	o = a.parentNode;
	
	for(var d in data){
		
		ele = a.insertRow(d);
		var stat = nullRemove(data[d].result);
		
		for("PASSED" == stat ? (ele.className = "PASSED", pass += 1) : "FAILED" == stat ? (ele.className = "FAILED", fail += 1, exceptionData(nullRemove(data[d].error))) : (ele.className = "SKIPPED", skip += 1), j = 1; j <= 6; j++){
			tab = ele.insertCell(ele.cells.length);
			var len,
				tag = document.createElement("div");
			if(stat == "PASSED"){
				tag.setAttribute("style", "background-color: #8cf28e");
			} else if(stat == "FAILED"){
				tag.setAttribute("style", "background-color: #f76d6b");
			}else{
				tag.setAttribute("style", "background-color: #f9f37f");
			}
				
			switch(j){
				case 1:
					len = nullRemove(data[d].count);
					break;
				case 2:
					len = nullRemove(data[d].name);
					break;
				case 3:
					len = nullRemove(data[d].method), methods.push(len);
					break;
				case 4:
					len = parseInt(nullRemove(data[d].duration)).toFixed(2),
						  seconds.push(parseInt(nullRemove(data[d].duration)));
					break;
				case 5:
					len = nullRemove(data[d].result);
					break;
				case 6:
					len = nullRemove(data[d].error);
					break;
			}
			
			tag.innerHTML = len,
			1 != j && 2 != j || (tag.className = "class"),
			3 != j && 4 != j || (tag.className = "result"),
			tab.appendChild(tag);
			
			if(d == 0){
				document.getElementById('page-id').innerHTML = data[d].suiteName;
			} 
			if(d == 0){
				document.getElementById('page-version-id').innerHTML = 'Version: ' + data[d].serverVersion;
			}
		
		}
	}
	o.appendChild(a);
}

function nullRemove(e){
	return null == e ? "" : e;
}

function toSecond(a) {
    return totaltime += parseInt(a),
        parseInt(a) / 1e3
} 

function parseTime(par){
	var y = par / 36e5,
	yr = Math.floor(y),
	hh = yr > 9 ? yr : "0" + yr,
	m = 60 * (y - yr),
	mr = Math.floor(m),
	mm = mr > 9 ? mr : "0" + mr,
	s = 60 * (m - mr),
	sr = Math.floor(s);
	return hh + ":" + mm + ":" + (sr > 9 ? sr : "0" + sr)
} 

function exceptionData(e){
	e.indexOf("Exception 1") > -1 || e.indexOf("Exception 2") > -1 ?
	Exception1 += 1 : e.indexOf("Exception 3") > -1 && e.indexOf("Exception 4") > -1 ?
	Exception2 += 1 : e.indexOf("Exception 5") > - 1? Exception2 += 1 : e.indexOf("Exception 6") > -1 ?
	Exception3 += 1 : e.indexOf("Exception 7") > -1 ? Exception3 += 1 : Exception4 += 1
}

function updateStatus(){
	var ele = {
		type: "pie",
		data:{
			datasets:[{
				data:[Exception1,Exception2,Exception3,Exception4],
				backgroundColor: ["#42a1f4","#42a1f4","#42a1f4","#42a1f4"],
				label: "Exceptions DataSet"
			}],
			labels: ["","","",""]},
			options:{
				pieceLabel:{
					render: "percentage",
					fontColor: ["whitesmoke","whitesmoke","whitesmoke"],
					precision: 1,
					fontSize: 12,
					fontStyle: "bold",
					overlap: !0
					},
				title: {
					dispaly: !1,
					text: "Exceptions"
				},
				legend: {
					labels:{
						boxWidth: 12,
						padding: 4,
						fontSize: 11
					}
				}
			}
		},
		tab = {
			type: "pie",
			data: {
				datasets:[{
					data: [pass,fail,skip],
					backgroundColor: ["#8cf28e", "#f76d6b", "#f9f37f"],
					label: "Status Dataset"
				}],
				labels: ["PASSED", "FAILED", "SKIPPED"]
			},
			options: {
				pieceLabel:{
					render: "percentage",
					fontColor: ["whilesmoke"],
					precision: 1,
					fontSize: 12,
					fontSyle: "bold",
					overlap: !0
				},
				title: {
					display: !1,
					text: "Results Chart Summary"
				},
				legend: {
					labels: {
						boxWidth: 28,
						padding: 6,
						fontSize: 12
					}
				}
			}
		},
		a = document.getElementById("test-summary-id").getContext("2d");
		
		window.myPie = new Chart(a, tab);
		
		var out = document.getElementById("result-exceptions-id").getContext("2d");
		
		window.myPie = new Chart(out, ele)
}