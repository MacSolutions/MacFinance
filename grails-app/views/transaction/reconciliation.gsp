
<%@page import="java.text.DecimalFormat"%>
<%@ page import="com.tracking.Transaction" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'transaction.label', default: 'Transaction')}" />
		<title>Reconciliation</title>
		<style type="text/css">
			body{
				width: 1350px;
			}
			td{
				max-width: 200px;
				overflow: hidden;
			}
		</style>
		<g:javascript library="prototype" />
		<g:javascript>
			function showTransaction(date,category,amount,name,transactionId){
				document.getElementById("transactionDate").innerHTML = date;
				document.getElementById("transactionAmount").innerHTML = amount;
				document.getElementById("transactionName").innerHTML = name;
				document.getElementById("transactionId").value = transactionId;  
				
			}
			function showBankRecord(date,amount,name,bankRecordId){
				document.getElementById("bankRecordDate").innerHTML = date;
				document.getElementById("bankRecordAmount").innerHTML = amount;
				document.getElementById("bankRecordName").innerHTML = name;
				document.getElementById("bankRecordId").value = bankRecordId;  
				
			}
		</g:javascript>
	</head>
	<body>
		<a href="#show-transaction" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<nav:render group="trackingTabs"/>
		</div>
		<div id="show-transaction" class="content scaffold-show" role="main" style="padding : 5px;">
			<h1>${application.monthNames[month]} Reconciliation</h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<br/>
			<div style="width:1
			00%; margin-bottom: 30px; height: 200px;">
				<div style = "width:43%; float:left; margin-left: 1%; height: 200px; border-style:outset; border-width: 10px; padding:5px;">
					<h3>Criteria</h3>
					<form action="reconciliation">
						<table>
							<thead>
								<tr>
									<th>Account</th>								
									<th>Month</th>								
									<th>Year</th>								
								</tr>
							</thead>
							<tr>
								<td>
									<g:select id="account" name="accountId" from="${com.tracking.Account.list()}" optionKey="id"  value="${accountId}" noSelection="['': 'Choose']"/>
								</td>
								<td>
									<g:select name="month" from="${application.monthNames}" optionKey="key" optionValue="value" noSelection="['': 'Choose']" value="${month}"/>
								</td>
								<td>
									<g:select name="year" from="${[2011,2012,2013,2014,2015]}" value="${year}" noSelection="['': 'Choose']" />
								</td>
							</tr>
							<tr>
								<td colspan="3" style="text-align: center;">
									<g:submitButton style="width:50%; background-color: #b8b9c8 " class= "refresh" name="Refresh" value = "Refresh"/>
								</td>
							</tr>
							<tr>
								<td colspan="3" style="text-align: center;">
									<g:remoteLink action = "toggleAllAccounts" update = "allAccounts">
										<div id = "allAccounts">
											<g:if test="${session.allAccounts}">
												Look at Transactions that only match Bank Record Accounts
											</g:if>
											<g:else>
												Look at Transactions from all Accounts
											</g:else>
										</div>
									</g:remoteLink>
								</td>
							</tr>
						</table>
					</form>
				</div>
				<div style = "width:49%; float:right; margin-right: 1%;  height: 200px; border-style:outset; border-width: 10px; padding:5px;">
					<g:form action = "verify">
						<g:hiddenField name="transactionId" value=""/>
						<g:hiddenField name="bankRecordId" value=""/>
						<g:hiddenField name="month" value="${month}"/>
						<g:hiddenField name="year" value="${year}"/>
						<g:hiddenField name="accountId" value="${accountId}" />
						<h3>Transaction/Bank Record Match</h3>
						<table>
							<thead>
								<tr>
									<th></th>
									<th>Date</th>
									<th>Amount</th>
									<th>Name/Description</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>Transaction</td>
									<td id="transactionDate"></td>
									<td id="transactionAmount"></td>
									<td id="transactionName"></td>
								</tr>
								<tr>
									<td>Bank Record</td>
									<td id="bankRecordDate"></td>
									<td id="bankRecordAmount"></td>
									<td id="bankRecordName"></td>
								</tr>
								<tr>
									<td colspan="4" style="text-align: center;">
										<g:submitButton style="width:50%; background-color: #b8b9c8 " name="Verify" value = "Verify"/>
									</td>
								</tr>
							</tbody>
						</table>					
					</g:form>			
				</div>
			</div>
			<div style="width:100%; padding-top:30px; height: 400px">
				
				<div id="transactions" style = "width:48%; height: 400px; overflow: scroll; float:left; margin-left: 1%;">
					<g:render template="reconTransactionList"/>
				</div>
				
				<div style = "width:48%; height: 400px; overflow: scroll; float:right; margin-right:1%;">
					<h3>Bank Records</h3>
					<table>
						<thead>
							<tr>
								<g:sortableColumn property="transactionDate" title="${message(code: 'bankRecord.transactionDate.label', default: 'Transaction Date')}" />
							
								<g:sortableColumn property="description" title="${message(code: 'bankRecord.description.label', default: 'Description')}" />
							
								<g:sortableColumn property="amount" title="${message(code: 'bankRecord.amount.label', default: 'Amount')}" />
							
							</tr>
						</thead>
						<tbody>
							<g:each in="${bankRecordInstanceList}" status="i" var="bankRecordInstance">
								<tr class="${(i % 2) == 0 ? 'even' : 'odd'}" 
								onclick="showBankRecord('${bankRecordInstance.transactionDate.format("MM-dd-yyyy")}','${bankRecordInstance.amount}','${bankRecordInstance.description.replace("\'", "")}','${bankRecordInstance.id}'),
								<g:remoteFunction action="getTransactionsForRecon" id="${bankRecordInstance.id}" update="transactions" /> ">
								
									<td><g:formatDate date="${bankRecordInstance.transactionDate}" format="MM-dd-yyyy"/>
								
									<td>${fieldValue(bean: bankRecordInstance, field: "description")}</td>
								
									<td><g:formatNumber number="${bankRecordInstance.amount}" type="currency" currencyCode="USD"/>
								
								</tr>
							</g:each>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</body>
</html>
