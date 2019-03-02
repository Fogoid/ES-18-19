package pt.ulisboa.tecnico.softeng.activity.domain

import pt.ist.fenixframework.FenixFramework
import pt.ist.fenixframework.core.WriteOnReadError

import javax.transaction.NotSupportedException
import javax.transaction.SystemException

abstract class SpockRollbackTestAbstractClass {

    def setUp() throws Exception {
        try {
            FenixFramework.getTransactionManager().begin(false)
            populate4Test()
        } catch (WriteOnReadError | NotSupportedException | SystemException el) {
            el.printStackTrace()
        }
    }

    def tearDown() {
        try{
            FenixFramework.getTransactionManager().rollback() 
        } catch (IllegalStateException | SecurityException | SystemException e){
            e.printStackTrace()
        }
    }

    def abstract populate4Test()
}
