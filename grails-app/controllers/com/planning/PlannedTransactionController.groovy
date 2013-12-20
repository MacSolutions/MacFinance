package com.planning

import org.springframework.dao.DataIntegrityViolationException

class PlannedTransactionController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	def plannedTransactionService
	static navigation=[
		[group:"planningTabs", action:"list", title:"Planned Transaction List", order:3]	
	]
    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [plannedTransactionInstanceList: PlannedTransaction.list(params), plannedTransactionInstanceTotal: PlannedTransaction.count()]
    }
	
	

    def create() {
		def budgetItem = BudgetItem.get(params.id)
//		Params
//		String view = params.createSet?:"create"
		
		render(view: "createSet", model: [plannedTransactionInstance: new PlannedTransaction(params), budgetItem:budgetItem])
    }

    def save() {
		
		def createResult = plannedTransactionService.createSet(params)
		
		flash.messages = createResult.messages
		flash.errors = createResult.errors
		
		render(view: "createSet", model: [plannedTransactionInstance: new PlannedTransaction(), budgetItem:BudgetItem.get(params.id)])

    }

    def show() {
        def plannedTransactionInstance = PlannedTransaction.get(params.id)
        if (!plannedTransactionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'plannedTransaction.label', default: 'PlannedTransaction'), params.id])
            redirect(action: "list")
            return
        }

        [plannedTransactionInstance: plannedTransactionInstance]
    }

    def edit() {
        def plannedTransactionInstance = PlannedTransaction.get(params.id)
		
        if (!plannedTransactionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'plannedTransaction.label', default: 'PlannedTransaction'), params.id])
            redirect(action: "list")
            return
        }

        [plannedTransactionInstance: plannedTransactionInstance]
    }

    def update() {
        def plannedTransactionInstance = PlannedTransaction.get(params.id)
        if (!plannedTransactionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'plannedTransaction.label', default: 'PlannedTransaction'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (plannedTransactionInstance.version > version) {
                plannedTransactionInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'plannedTransaction.label', default: 'PlannedTransaction')] as Object[],
                          "Another user has updated this PlannedTransaction while you were editing")
                render(view: "edit", model: [plannedTransactionInstance: plannedTransactionInstance])
                return
            }
        }

		bindData(plannedTransactionInstance ,params,['amount'])
		def editResult = plannedTransactionService.editAmount(plannedTransactionInstance, params)
		if(editResult.errors) flash.errors = editResult.errors

        if (flash.errors || !plannedTransactionInstance.save(flush: true)) {
            render(view: "edit", model: [plannedTransactionInstance: plannedTransactionInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'plannedTransaction.label', default: 'PlannedTransaction'), plannedTransactionInstance.id])
        redirect(action: "show", id: plannedTransactionInstance.id)
    }

    def delete() {
        def plannedTransactionInstance = PlannedTransaction.get(params.id)
        if (!plannedTransactionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'plannedTransaction.label', default: 'PlannedTransaction'), params.id])
            redirect(action: "list")
            return
        }

        try {
            plannedTransactionInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'plannedTransaction.label', default: 'PlannedTransaction'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'plannedTransaction.label', default: 'PlannedTransaction'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
	
	def search(){
		Boolean validSearch = true
		Double amount
		flash.searchErrors = [:]
		if(params.amount){
			try{
				amount = params.amount.toDouble()
			}catch(Exception ex){
				validSearch	
				flash.searchErrors.amount = "Amount must be a valid number with no symbols other than one '.'"
			}
 		}
		
		if(validSearch){
			params.each{
				println it
			}
			
			def c = PlannedTransaction.createCriteria()
			params.max = Math.min(params.max ? params.int('max') : 10, 100)
			
			List plannedTransactions = c.list (params){
				if(params.amount){
					println "Searching Amount"
					between("amount",amount - 10, amount + 10)
				}
				if(params.cash != 'null'){
					println "Searching Cash "
					eq("cash", params.cash)
				}
				if(params.exempt != 'null'){
					println "Searching exempt "
					eq("exempt", params.exempt)
				}
				if(params.budgetItemId != 'null'){
					budgetItem{
						eq("id",params.budgetItemId.toLong())
					}
				}
				if(params.dateSearchOption != 'null'){
					println "Searching Date "
					if(params.dateSearchOption == "By Month"){
						Map dateRange = plannedTransactionService.getSearchDateRange(params)
						between("plannedTransactionDate",dateRange.startDate,dateRange.endDate)
					}else if(params.dateSearchOption == "By Date"){
						println "finding ${params.date}"
						eq("plannedTransactionDate", params.date)
					}else if(params.dateSearchOption == "By Date Range"){
						between("plannedTransactionDate",params.startDate,params.endDate)
					}
				}
			}
			
			println "Planned Transactions Count ${plannedTransactions.size()}"
			render(view:"list" , model :  [plannedTransactionInstanceList: plannedTransactions, plannedTransactionInstanceTotal: plannedTransactions.getTotalCount()])
		}
		else{
			redirect(action:"list")
		}
	}
	
	
}
