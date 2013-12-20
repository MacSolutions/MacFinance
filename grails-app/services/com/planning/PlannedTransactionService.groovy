package com.planning
import com.category.Category

class PlannedTransactionService {

	Date currentDate 

	def createSet(Map params) {
		println "Creating Set"
		BudgetItem budgetItem = BudgetItem.get(params.id)
		Map errors = [:]
		Integer errorIterator = 1
		Map messages = [:]
		Integer messageIterator = 1
		List plannedTransactions = []
		List newPlannedTransactions = []
		Double amount = 0
		Category categoryInstance = budgetItem.category
		currentDate = new Date()

		if(params.amountOption == "addAmount"){
			try{
				amount = params.amount.toDouble()
				if(amount < 0) throw new NumberFormatException()
			}catch(Exception ex){
				errors.inValidAmount = "Invalid Amount"
			}
		}

		if(params.startDate.clearTime() < currentDate.clearTime()){
			errors.inValidStartDateType1 = "Start Date must be current or future date"
		}
		
		if(params.startDate > params.endDate){
			errors.inValidStartDateType2 = "Start Date must be before end date"
		}
		
		Date testDate = new Date()
		testDate.set(year:budgetItem.year,month:budgetItem.month-1)
		if(testDate.format("MM").toInteger() != params.startDate.format("MM").toInteger()){
			println "${testDate}.....${params.startDate}"
			errors.inValidStartDateType3 = "Start Date must be within the month of the chosen budget item"
		}
		
		
		if(params.replaceCurrentSet){
			List currentSet = PlannedTransaction.withCriteria {
				between("plannedTransactionDate",params.startDate,params.endDate)
				category{
					eq("id",categoryInstance.id)
				}
			}		
			
			currentSet.each{ plannedTransaction ->
				
				plannedTransaction.delete(flush:true)
			}
		}
		
		if(errors){
			println errors
			return [errors:errors]
		}
		
		currentDate = params.startDate
		println budgetItem
		while(currentDate <= params.endDate && !errors.inValidStartDate){
			
			Integer year = currentDate.format("yyyy").toInteger()
			Integer month = currentDate.format("MM").toInteger() 
			if(budgetItem.year != year || budgetItem.month != month){
				List budgetItems = BudgetItem.withCriteria {
					eq("year",year)
					eq("month",month)
					category{
						eq("id",categoryInstance.id)
					}
				}
				
				if(budgetItems){
					budgetItem = budgetItems.get(0)
					println budgetItem
				}else{
					
					
					errors."missingBudgetItem${errorIterator++}" = "No ${categoryInstance} Budget Item exists for ${month}/${year}<br/>"
					
					println "No ${categoryInstance} budget exists for ${month}/${year}"
					while(currentDate.format("yyyy").toInteger() == year && currentDate.format("MM").toInteger() == month){
						getNextDate(params.frequencyOption)
					}
					continue
				}
			}
			
			PlannedTransaction plannedTransaction = new PlannedTransaction(plannedTransactionDate:currentDate.clearTime(),category:categoryInstance, budgetItem:budgetItem)
			plannedTransactions << plannedTransaction
			
			getNextDate(params.frequencyOption)
			
			if(budgetItem.year != currentDate.format("yyyy").toInteger() || budgetItem.month != currentDate.format("MM").toInteger()){
				println "All Planned transaction for ${budgetItem} have been created ${plannedTransactions.size()} total"
				messages."message${messageIterator++}" = "${plannedTransactions.size()} planned transaction created for ${budgetItem}"
				if(params.amountOption == "deriveAmount"){
					println "calculating amount using budget item total"
					amount = budgetItem.amount/plannedTransactions.size()
					println "amount: ${amount}"
				}
				
				plannedTransactions.each{
					it.amount = amount
					it.save(flush:true)
					newPlannedTransactions << it
				}
				
				if(params.amountOption == "addAmount"){
					budgetItem.calculateAmount()
				}
				
				plannedTransactions = []
			}
		}
		
		return [errors : errors, messages:messages]
	}

	def getNextDate(String frequencyOption){
		if(frequencyOption == "daily"){
			currentDate = currentDate + 1
		}else if(frequencyOption == "biDaily"){
			currentDate = currentDate + 2
		}else if(frequencyOption == "weekly"){
			currentDate = currentDate + 7
		}else if(frequencyOption == "biWeekly"){
			currentDate = currentDate + 14
		}else if(frequencyOption == "monthly"){
			Integer year = currentDate.format("yyyy").toInteger()
			Integer month = currentDate.format("MM").toInteger()
			if(currentDate.format("MM").toInteger() == 12){
				year++
				currentDate.set("year":year)
				month = 0
			}
			month++
			currentDate.set("month":month - 1)
		}//else if(frequencyOption == "semiMonthly"){
		//		}
	}
	
	def getSearchDateRange(Map params){
		Date startDate
		Date endDate
		
		def month = params.month.format("MM").toInteger()
		def year = params.month.format("yyyy").toInteger()
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		def firstOfTheMonth = new Date()
		firstOfTheMonth.set(year: year, month: month-1, date:1)
		def endOfTheMonth = new Date()
		endOfTheMonth.set(year: year, month: month-1, date:days)
		
		[startDate:firstOfTheMonth.clearTime(),endDate:endOfTheMonth.clearTime()]
	}
	
	def editAmount(PlannedTransaction plannedTransaction, Map params){
		println params.amount
		def errors = [:]
		def amount
		try{
			amount = params.amount.toDouble()
			if(amount < 0) throw new NumberFormatException()
			plannedTransaction.amount = amount
			plannedTransaction.budgetItem.calculateAmount()
		}catch(Exception ex){
			errors.inValidAmount = "Invalid Amount"
		}
		
		[errors:errors]
	}
}
