package pt.ulisboa.tecnico.softeng.bank.domain

import org.omg.CORBA.SystemException
import pt.ist.fenixframework.FenixFramework
import pt.ist.fenixframework.core.WriteOnReadError
import spock.lang.Specification

import javax.transaction.NotSupportedException

abstract class SpockRollbackTestAbstractClass extends Specification {
    def setup() throws Exception {
      try {
          FenixFramework.getTransactionManager().begin(false)
          "populate for test"()
      } catch (WriteOnReadError | NotSupportedException | SystemException e1) {
            e1.printStackTrace()
      }
    }

    def tearDown() {
        try {
            FenixFramework.getTransactionManager().rollback()
        } catch (IllegalStateException | SecurityException | SystemException e) {
            e.printStackTrace()
        }
    }

    abstract def "populate for test"()
}
