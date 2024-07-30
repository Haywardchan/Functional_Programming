package ass3

/**
 * This class is a test suite for the methods in object Lisp.
 *
 * To run this test suite, start "sbt" then run the "test" command.
 */
class LispSuite extends munit.FunSuite:
  import language.deprecated.symbolLiterals
  import Lisp.evaluate
  
  test("Field selection 1") {
    assertEquals(evaluate("(class (Pair x y) (val p (Pair \"zero\" 0) (sel p x))))))"), "zero")
    assertEquals(evaluate("(class (Pair x y) (val p (Pair \"zero\" 0) (sel p y))))))"), "0")
  }
  test("Field selection 2") {
    assertEquals(evaluate("(class (Pair x y) (class (Riap y x) (def f (lambda (p) (sel p x)) (val p (Pair 2 3) (val r (Riap 2 3) (* (f p) (f r)))))))"), "6")
  }
  test("Field Selection 3") {
    assertEquals(evaluate("(class (Pair x y) (val p (Pair 1 2) (class (Pair y x) (sel p x))))"), "1")
    assertEquals(evaluate("(class (Pair x y) (val p (Pair \"zero\" (+ 1 1)) (sel p y))))))"), "2")
    assertEquals(evaluate("(class (Pair x y) (val p (Pair \"zero\" (* (+ 1 1) 10)) (sel p y))))))"), "20")
    // intercept[IndexOutOfBoundsException]{
      // evaluate("(class (Pair x y) (val p (Pair 1 2) (class (Pair x y) (sel p z))))")//field
      // evaluate("(class (Pair x y) (val p (2) (class (Pair x y) (sel p z))))")//sel
    // }
  }
  test("Pattern matching trial 1") {
    assertEquals(evaluate("(class (Single x) (case (Single 1) ((Single a) (+ a 1))))"), "2")
  }
  test("Pattern matching trial 2") {
    assertEquals(evaluate("(class (Pair x y) (case (Pair 1 2) ((Pair a b) (+ a b)))))"), "3")
  }
  test("Pattern matching trial 3") {
    assertEquals(evaluate("(class (Pair x y) (def prod (lambda (x) (case x ((Pair x y) (* x y)))) (val x (Pair 2 3) (prod x)))))"), "6")
  }
  test("Pattern matching trial 4") {
    assertEquals(evaluate("(class (Triple x y z) (def prod (lambda (x) (case x ((Triple x y z) (* z (* x y))))) (val x (Triple 2 3 7) (prod x)))))"), "42")
  }
  test("Pattern matching 1") {
    assertEquals(evaluate("(class (Pair x y) (class (Triple x y z) (def prod (lambda (x) (case x ((Pair x y) (* x y)) ((Triple x y z) (* (* x y) z)))) (val x (Triple 2 3 7) (prod x)))))"), "42")
  }

  test("Pattern matching 2") {
    assertEquals(evaluate("(class (Pair x y) (class (Triple x y z) (def f (lambda (x) (case x ((Pair x y) (* x y)) (x (cons x nil)))) (val x (Triple 2 3 7) (sel (car (f x)) z)))))"), "7")
  }

  test("Call-by-need: no need 1") {
    assertEquals(evaluate("(def zero (lambda (x) 0) (zero nani))"), "0")
  }

  test("Call-by-need: no need 2") {
    assertEquals(evaluate("(class (Just x) (val x (Just nani) 0))"), "0")
  }

  test("Call-by-need: no need 3") {
    assertEquals(evaluate("(class (Pair x y) (def zero (lambda (x) 0) (val x (Pair nani1 nani2) (zero (sel x x)))))"), "0")
  }

  test("Call-by-need: no need 4") {
    assertEquals(evaluate("(class (Pair x y) (def zero (lambda (x) 0) (case (Pair <expr1> <expr2>) ((Pair a b) (zero a)))))"), "0")
  }

  test("Call-by-need: no need 5") {
    assertEquals(evaluate("(class (Pair x y) (val x (Pair (+ 1 1) <expr2>) (* (sel x x) (sel x x))))"), "4")
  }
  
  test("Call-by-need: no need 6") {
    assertEquals(evaluate("(class (Pair x y) (case (Pair 2 <expr2>) ((Pair a b) (* a a))))"), "4")
  }

  test("Call-by-need 01"){
    assertEquals(evaluate("(def sq (lambda (x) (* x x)) (sq (sq (sq 2))))"), "256")
  }

  test("call-by-need 02") {
    assertEquals(evaluate("(val x (* 2 2) (* x x))"), "16")
  }

  test("Call-by-need: val binding not evaluated") {
    assertEquals(evaluate("(val x <expr> 0)"), "0")
  }

  test("Call-by-need: val binding evaluated once") {
    assertEquals(evaluate("(val x 7 (* x x))"), "49")
  }

  test("Call-by-need: function argument not evaluated") {
    assertEquals(evaluate("(def id (lambda (x) x) (def zero (lambda (x) 0) (zero (id wtfffff))))"), "0")
  }

  test("Call-by-need: function argument evaluated once") {
    assertEquals(evaluate("(def sq (lambda (x) (* x x)) (sq (sq (sq 1))))"), "1")
  }

  test("Call-by-need: constructor argument not evaluated") {
    assertEquals(evaluate("(class (Pair x y) (def zero (lambda (x) 0) (val x (Pair <expr1> <expr2>) (zero (sel x x)))))"), "0")
  }

  test("Call-by-need: constructor argument evaluated once") {
    assertEquals(evaluate("(class (Pair x y) (val x (Pair 9 <expr2>) (* (sel x x) (sel x x))))"), "81")
  }
