# Principles of programming language

start scala REPL by simply typing sbt in the terminal

#### Substitution model
A non-primitive expression is evaluated as follows.
1. Take the leftmost operator
2. Evaluate its operands (left before right)
3. Apply the operator to the operands

Applications of parameterized functions are evaluated in a similar way as
operators:
1. Evaluate all function arguments, from left to right
2. Replace the function application by the function’s right-hand side,
and, at the same time
3. Replace the formal parameters of the function by the actual
arguments.

### Termination
Call-by-name and call-by-value
Both strategies reduce to the same final values as long as
1. the reduced expression consists of pure functions, and
2. both evaluations terminate.

call-by-value (x:Int)
- evaluates every function argument only **once**

Call-by-name (x:=>Int)
- function arguments are **not evaluated** if parameter is unused in evaluation of function body

### Conditionals and Value Definitions
def Call by Name

val Call by Value
- all are expressions: (predicate)?a:b becomes if predicate then a else b
```scala
def and (x: Boolean, y: Boolean): Boolean=
    if x then y else false
def or (x: Boolean, y: Boolean): Boolean=
    if x then true else if y then true else false
```
### Blocks and Lexical Scope
- Iteration can be defined within functions
- Block definitions are only visible within the block
```scala
def sqrt(x: Double) =
    def sqrtIter(guess: Double): Double =
        if isGoodEnough(guess) then guess
        else sqrtIter(improve(guess))
    def improve(guess: Double) =
        (guess + x / guess) / 2
    def isGoodEnough(guess: Double) =
        abs(square(guess) - x) < 0.001
    sqrtIter(1.0)
end sqrt
```
### Tail recursion
- If a function calls itself as its last action, the function’s stack frame can be
reused.

- only direct recursion calls to current function is optimized

These have to be added in the implementation before a tail recursive function
```scala
import scala.annotation.tailrec
@tailrec
def gcd(a: Int, b: Int): Int =
    if b == 0 then a else gcd(b, a % b)

@tailrec
def factorial(n: Int): Int =
    if n == 0 then 1 else n * factorial(n - 1)
```
### Higher order functions
- Functions that take function arguments 
### Anonymous functions
(Inputs)=>(Outputs)

I.e x => x * x * x
### Currying
- functions that further return functions
```scala
def mapReduce(f:Int=>Int, combine: (Int, Int) => Int, zero: Int)(a:Int, b:Int)=
    def recur(a:Int):Int=
        if a>b then zero
        else combine(f(a), recur(a+1))
    recur(a)
def sum(f:Int=>Int)=mapReduce(f, (x,y)=>x+y, 0)
def poduct(f:Int=>Int)=mapReduce(f, (x,y)=>x*y, 1)
```

### Finding fixed points
```scala
val tolerance = 0.0001
def isCloseEnough(x: Double, y: Double) =
    abs((x - y) / x) < tolerance
def fixedPoint(f: Double => Double)(firstGuess: Double): Double =
    def iterate(guess: Double): Double =
        val next = f(guess)
        if isCloseEnough(guess, next) then next
        else iterate(next)
    iterate(firstGuess)
// ver 1 (Converges successful but can be improved)
def sqrt(x: Double) = fixedPoint(y => (y + x / y) / 2)(1.0)
// ver 2 (With more abstractions)
def averageDamp(f: Double => Double)(x: Double): Double =
    (x + f(x)) / 2
def sqrt(x: Double) = fixedPoint (averageDamp (y => x/y)) (1.0)
```

### Functions and Data
- In scala we can define a class of data structure with its own defined methods
```scala
class Rational(x: Int, y: Int):
    def numer = x
    def denom = y
    def add(r: Rational) =
        Rational(numer * r.denom + r.numer * denom,
        denom * r.denom)
    def mul(r: Rational) =
        Rational(numer*r.numer, denom*r.denom)
    def neg = Rational(-numer, denom)
    def sub(r:Rational) = add(r.neg)
    override def toString = s”$numer/$denom”
```

### Data abstraction
```scala
class Rational(x: Int, y: Int):
    require(y > 0, ”denominator must be positive”)  // IllegalArgumentException
    val k = sqrt(y)
    assert(k >= 0)  // AssertionError
    def this(x: Int) = this(x, 1)   // Auxiliary constructors
    private def gcd(a: Int, b: Int): Int =
        if b == 0 then a else gcd(b, a % b)
    val numer = x / gcd(x.abs, y) // val is used instead of def for evaluating values once only
    val denom = y / gcd(x.abs, y) // abs is needed
    def less(that: Rational): Boolean =
        numer * that.denom < that.numer * denom
    def max(that: Rational): Rational =
        if this.less(that) then that else this
end Rational // End marker
```
The primary constructor
1. takes the parameters of the class
2. and executes all statements in the class body (such as the require a
couple of slides back).

- Due to problem of overflow, if simplifications are applied for tostring method then large sizes of denominators and numerators may not work well

### Evaluation and Operators
```Scala
Rational(1, 2).numer
[1/x, 2/y] [] [Rational(1, 2)/this] x = 1
Rational(1, 2).less(Rational(2, 3))
[1/x, 2/y] [Rational(2, 3)/that] [Rational(1, 2)/this] = 1*3<2*2 = true
Rational(1, 2).min(Rational(2, 3))
[Rational(1, 2)/r] [Rational(2, 3)/s] if x.less(r) then s else r

extension (r: Rational)
    def min(s: Rational): Rational = if s.less(r) then s else r
    def abs: Rational = Rational(r.numer.abs, r.denom)

extension (x: Rational)
    // Relaxed Identifers
    def + (y: Rational): Rational = x.add(y)
    def * (y: Rational): Rational = x.mul(y)
    // Infix Notation (r min s) can be used
    infix def min(that: Rational): Rational = ...
```
Caveats:
1. Extensions can only add new members, not override existing ones.
2. Extensions cannot refer to other class members via this

Precidence rules
```
(all letters)
|
^
&
< >
= !
:
+ -
*/%
(all other special characters)
Example:
((a + b) ^? (c ?^ d)) less ((a ==> b) | c)
```
### Class Hierarchy
```scala
abstract class IntSet:
    def incl(x: Int): IntSet
    def contains(x: Int): Boolean
    def union(other: IntSet): IntSet

class Empty() extends IntSet:
    def contains(x: Int): Boolean = false
    def incl(x: Int): IntSet = NonEmpty(x, Empty(), Empty())
end Empty
// Since only one Empty object is needed:
object Empty extends IntSet:
    def contains(x: Int): Boolean = false
    def incl(x: Int): IntSet = NonEmpty(x, Empty, Empty)
    def union(other: IntSet): IntSet = other
end Empty

class NonEmpty(elem: Int, left: IntSet, right: IntSet) extends IntSet:
    def contains(x: Int): Boolean =
        if x < elem then left.contains(x)
        else if x > elem then right.contains(x)
        else true
    def incl(x: Int): IntSet =
        if x < elem then NonEmpty(elem, left.incl(x), right)
        else if x > elem then NonEmpty(elem, left, right.incl(x))
        else this
    //pretty hard to understand
    def union(other: IntSet): IntSet =
        left.union(right).union(other).incl(elem)
end NonEmpty
// companions (class and object with same name)
// Classes: type namespace; Objects: term namespace (2 namespaces in scala)
object IntSet:
    def singleton(x: Int) = NonEmpty(x, Empty, Empty)
// call this by scala Hello
object Hello:
    def main(args: Array[String]): Unit = println(”hello world!”)
// take cmd args, call by scala birthday Peter 11
@main def birthday(name: String, age: Int) =
    println(s”Happy birthday, $name! $age years old already!”)
```
1. Abstract class can contain non-implemented members 
2. No instance can be instantiated within an abstract class

##### Dynamic method dispatch
This means that the code invoked by a method call depends on the
runtime type of the object that contains the method.
```scala
Empty.contains(1)
[1/x] [Empty/this] false
(NonEmpty(7, Empty, Empty)).contains(7)
[7/elem] [7/x] [new NonEmpty(7, Empty, Empty)/this]
```
### Class Organizations
```scala
package ppl.examples            // put a class/ object into a package
import week3.Rational           // imports just Rational
import week3.{Rational, Hello}  // imports both Rational and Hello
import week3.*                  // imports everything in package week3
//Automatic imports (www.scala-lang.org/api/current)
scala, java.lang, scala.Predef  // 3 large packages
Int         scala.Int
Boolean     scala.Boolean
Object      java.lang.Object
require     scala.Predef.require
assert      scala.Predef.assert
// An object/ class can inherit multiple traits 
trait Planar:
    def height: Int
    def width: Int
    def surface = height * width

class Square extends Shape, Planar, Movable ...

```
### Scala's hierarchy
```scala
Scala.Any{
    scala.AnyVal{
        scala.Double
        scala.Float
        scala.Long
        scala.Int<->scala.Char
        scala.Short
        scala.Byte
        scala.Boolean
        scala.Unit
    }
    scala.AnyRef{ //java.lang.Object
        scala.iterable
        scala.Seq
        scala.List
        java.lang.String
    }
    scala.Nothing
}
throw exn //throws exception with type Nothing
if true then 1 else false // type: AnyVal for the entire expression
```
- Scala.Nothing is a subtype of every other type
1. To signal abnormal termination
2. As an element type of empty collections (see next session)

### Polymorphism
Cons-Lists
```scala
// Nil the empty list
// Cons a cell containing an element and the remainder of the list.
List(1, 2, 3) == Cons(1, Cons(2, Cons(3, Nil())))

List(List(true, false), List(3)) == 
Cons( Cons(true, Cons(false, Nil())), Cons( Cons(3, Nil())), Nil())

class Cons(_head: Int, _tail: IntList) extends IntList:
    val head = _head
    val tail = _tail

package week3
trait List[T]
class Cons[T](val head: T, val tail: List[T]) extends List[T]
class Nil[T] extends List[T]

trait List[T]:
    def isEmpty: Boolean
    def head: T
    def tail: List[T]
class Cons[T](val head: T, val tail: List[T]) extends List[T]:
    def isEmpty = false
class Nil[T] extends List[T]:
    def isEmpty = true
    def head = throw new NoSuchElementException(”Nil.head”)
    def tail = throw new NoSuchElementException(”Nil.tail”)

def singleton[T](elem: T) = Cons[T](elem, Nil[T])
singleton[Int](1)
singleton[Boolean](true)

def nth[T](xs: List[T], n: Int): T =
    if xs.isEmpty then throw IndexOutOfBoundsException()
    else if n == 0 then xs.head
    else nth(xs.tail, n - 1)

```

### Objects everywhere
```scala
package idealized.scala
abstract class Boolean extends AnyVal:
    def ifThenElse[T](t: => T, e: => T): T
    def && (x: => Boolean): Boolean = ifThenElse(x, false)
    def || (x: => Boolean): Boolean = ifThenElse(true, x)
    def unary_!: Boolean = ifThenElse(false, true)
    def == (x: Boolean): Boolean = ifThenElse(x, x.unary_!)
    def != (x: Boolean): Boolean = ifThenElse(x.unary_!, x)
    object true extends Boolean:
    def ifThenElse[T](t: => T, e: => T)=t
    object false extends Boolean:
    def ifThenElse[T](t: => T, e: => T)=e
end Boolean

extension (x: Boolean)
def ==> (y: Boolean) = x.ifThenElse(y, true)
// implementation of Natural Numbers
abstract class Nat:
def isZero: Boolean
def predecessor: Nat
def successor: Nat
def + (that: Nat): Nat
def - (that: Nat): Nat
end Nat

object Zero extends Nat:
    def isZero: Boolean = true
    def predecessor: Nat = ???
    def successor: Nat = Succ(this)
    def + (that: Nat): Nat = that
    def - (that: Nat): Nat = if that.isZero then this else ???

object Succ(n: Nat) extends Nat:
    def isZero: Boolean = false
    def predecessor: Nat = n
    def successor: Nat = Succ(this)
    def + (that: Nat): Nat = Succ(n + that)
    def - (that: Nat): Nat =
    if that.isZero then this else n - that.predecessor

```
### Functions as objects
- Functions can be treated as objects
```scala
package scala
trait Function1[A, B]:
    def apply(x: A): B

(x: Int) => x * x

new Function1[Int, Int]:
    def apply(x: Int)=x*x

{
class $anonfun() extends Function1[Int, Int]:
    def apply(x: Int)=x*x
    $anonfun()
}

def f(x: Int): Boolean = ... // is not a function value
    (x: Int) => f(x) // f is a function value if f is used where a function is expected
new Function1[Int, Boolean]:
    def apply(x: Int) = f(x)
// implement IntSet Constructors:
//IntSet() IntSet(1) IntSet(2, 3)
object IntSet:
    def apply(): IntSet = Empty
    def apply(x: Int): IntSet = apply().incl(x)
    def apply(x: Int, y: Int): IntSet = apply(x).incl(y)
```
### Decomposition
```scala
abstract class Expr:
    def isNumber: Boolean
    def isSum: Boolean
    def numValue: Int
    def leftOp: Expr
    def rightOp: Expr

class Number(n: Int) extends Expr:
    def isNumber = true
    def isSum = false
    def numValue = n
    def leftOp = throw Error(”Number.leftOp”)
    def rightOp = throw Error(”Number.rightOp”)

class Sum(e1: Expr, e2: Expr) extends Expr:
    def isNumber = false
    def isSum = true
    def numValue = throw Error(”Sum.numValue”)
    def leftOp = e1
    def rightOp = e2

// tedious, some case maybe forgotten, may not use correct ancessor function
def eval(e: Expr): Int =
    if e.isNumber then e.numValue
    else if e.isSum then eval(e.leftOp) + eval(e.rightOp)
    else throw Error(”Unknown expression ” + e)

// ulgy and potentially unsafe
def eval(e: Expr): Int =
    if e.isInstanceOf[Number] then
        e.asInstanceOf[Number].numValue
    else if e.isInstanceOf[Sum] then
        eval(e.asInstanceOf[Sum].leftOp)
        + eval(e.asInstanceOf[Sum].rightOp)
    else throw Error(”Unknown expression ” + e)
```

```scala
//Solution 1 OOP decomposition
abstract class Expr:
    def eval: Int
class Number(n: Int) extends Expr:
    def eval: Int = n
class Sum(e1: Expr, e2: Expr) extends Expr:
    def eval: Int = e1.eval + e2.eval
```
1. mix operation with data
2. increase complexity, add new class dependency, encapsulation + inheritence
3. easy to add new kinds of data but not operations
4. only **single** object can work well (cant work in simplifying expression)
5. cannot inspect arguments
```scala
//Solution 2 Functional decomposition with pattern matching
abstract class Expr
    case class Number(n: Int) extends Expr
    case class Sum(e1: Expr, e2: Expr) extends Expr
//MatchError exception is thrown if no pattern matches the value of the selector
def eval(e: Expr): Int = e match
    case Number(n) => n
    case Sum(e1, e2) => eval(e1) + eval(e2) 
```
We can type match:
- constructors (Number, Sum)
> matches all values of type/ subtype constructed by C(p1..pn)
- variables (n, e1, e2)
- wildcard pattern (_)
- constants (1, false)
- type tests (n: Number)
```scala
// example of expansion
eval(Sum(Number(1), Number(2)))
→
Sum(Number(1), Number(2)) match
    case Number(n) => n
    case Sum(e1, e2) => eval(e1) + eval(e2)
→
eval(Number(1)) + eval(Number(2))
→
(Number(1) match
    case Number(n) => n
    case Sum(e1, e2) => eval(e1) + eval(e2)
) + eval(Number(2))
→
1 + eval(Number(2))
→→
3
```
> Seal the data structure to prohibit definition so match error will not pop up when there is a match error
```scala
//sealed data structure
sealed abstract class Expr
    case class Number(n: Int) extends Expr
    case class Sum(e1: Expr, e2: Expr) extends Expr
//Also possible to put the function as a method of base trait
sealed abstract class Expr:
    def eval: Int = this match
        case Number(n) => n
        case Sum(e1, e2) => e1.eval + e2.eval
```
Q: Write a function to return the representation of expression as a string
```scala
def show(e: Expr): String = e match
    case Number(n) => n.toString
    case Sum(e1, e2) =>
    show(e1) + ”+” + show(e2)
    // or, equivalently:
    s”${ show(e1) } + ${ show(e2) }”
```
Q: Change it so it can also deal with products and preserve the priority of which
```scala
sealed abstract class Expr
case class Number(n: Int) extends Expr
case class Sum(e1: Expr, e2: Expr) extends Expr
case class Product(e1: Expr, e2: Expr) extends Expr
case class Var(name: String) extends Expr

def show(e: Expr): String = e match
    case Number(n) => n.toString
    case Sum(e1, e2) => s”${ show(e1) } + ${ show(e2) }”
    case Product(e1, e2) => s”${ showFactor(e1) } * ${ showFactor(e2) }”
case Var(n) => n

def showFactor(e: Expr): String = e match
    case e: Sum => s”(${ show(e) })”
    case _ => show(this)
```
### Lists
- immutable, recursive (nested, unlike flat array)
- homogeneous (can only contain the same type)
- constructed by Nil and Cons function ::
```scala
val fruit = List(”apples”, ”oranges”, ”pears”)
//homogeneous (specify the type)
val diag3: List[List[Int]] = List(List(1, 0, 0), List(0, 1, 0), List(0, 0, 1))
val empty: List[Nothing] = List()
//construction (Associate to the right)
fruit = ”apples” :: (”oranges” :: (”pears” :: Nil))
nums = 1 :: (2 :: (3 :: (4 :: Nil)))
empty = Nil
//operations
//head return first element
//tail return all except first element
//isEmpty return true if empty else false
fruit.head == ”apples”
fruit.tail.head == ”oranges”
diag3.head == List(1, 0, 0)
empty.head == throw NoSuchElementException(”head of empty list”)
//can use as decomposition
1 :: 2 :: xs    Lists of that start with 1 and then 2
x :: Nil        Lists of length 1
List(x)         Same as x :: Nil
List()          The empty list, same as Nil
List(2 :: xs)   A list that contains as only element another list that starts with 2.
```
```scala
//Sorting list O(n^2)
def insert(x: Int, xs: List[Int]): List[Int] = xs match
    case Nil => List(x)
    case y :: ys =>
    if x<y then x :: xs else y :: insert(x, ys)
```
#### Other Operation implementation of list
```scala
//get the last elem in list O(n)
def last[T](xs: List[T]): T = xs match
    case List() => throw Error(”last of empty list”)
    case List(x) => x
    case y :: ys => last(ys)
//get the whole list except the last elem
def init[T](xs: List[T]): List[T] = xs match
    case List() => throw Error(”init of empty list”)
    case List(x) => List()
    case y :: ys => y :: init(ys)
//concat as extension O(n)
extension [T](xs: List[T])
    def ++ (ys: List[T]): List[T] = xs match
        case Nil => ys`
        case x :: xs1 => x :: (xs1 ++ ys)
//reverse List O(n^2)
extension [T](xs: List[T])
    def reverse: List[T] = xs match
        case Nil => Nil
        case y :: ys => ys.reverse ++ List(y)
//remove elem in certain index
def removeAt[T](n: Int, xs: List[T]) = xs match
    case Nil => Nil
    case y :: ys =>
        if n == 0 then ys
        else y :: removeAt(n - 1, ys)
//flatten a list
def flatten(xs: Any): List[Any] = xs match
    case Nil => Nil
    case y :: ys => flatten(y) ++ flatten(ys)
    case nonList => nonList :: Nil
```
#### Higher Order List Function
```scala
//Mapping
extension [T](xs: List[T])
    def map[U](f: T => U): List[U] = xs match
        case Nil => Nil
        case x :: xs => f(x) :: xs.map(f)
//Filtering
extension [T](xs: List[T])
    def filter(p: T => Boolean): List[T] = this match
        case Nil => xs
        case x :: xs => if p(x) then x :: xs.filter(p) else xs.filter(p)
//Utilizing the map and filter function
def squareList(xs: List[Int]): List[Int] = xs match
    case Nil => Nil
    case y :: ys => y * y :: squareList(ys)
    xs.map(x => x * x)//same
def posElems(xs: List[Int]): List[Int] = xs match
    case Nil => xs
    case y :: ys => if y>0 then y :: posElems(ys) else posElems(ys)
    xs.filter(x => x > 0)//same
//Packing same elem into multi dimensional lists
def pack[T](xs: List[T]): List[List[T]] = xs match
    case Nil => Nil
    case x :: xs1 =>
        val (more, rest) = xs1.span(y => y == x)
        (x :: more) :: pack(rest)
//Returns a multi dimensional tuple of (elem, count)
def encode[T](xs: List[T]): List[(T, Int)] =
    pack(xs).map(ys => (ys.head, ys.length))
```
### Reduction on list 
```scala
//recursion approach
def sum(xs: List[Int]): Int = xs match
    case Nil => 0
    case y :: ys => y + sum(ys)
//By using reduceleft we can further simplify the implementation
List(x1, ..., xn).reduceLeft(op) = x1.op(x2). ... .op(xn)
def sum(xs: List[Int]) = (0 :: xs).reduceLeft((x, y) => x + y)
def product(xs: List[Int]) = (1 :: xs).reduceLeft((x, y) => x * y)
>>> Cannot deal with Empty list!!!
//using short hand functions
def sum(xs: List[Int]) = (0 :: xs).reduceLeft(_ + _)
def product(xs: List[Int]) = (1 :: xs).reduceLeft(_ * _)
//FoldLeft is a better choice, it return the accumulator[the first bracket] if the input is an empty list
List(x1, ..., xn).foldLeft(z)(op) = z.op(x1).op ... .op(xn)
def sum(xs: List[Int]) = xs.foldLeft(0)(_ + _)
def product(xs: List[Int]) = xs.foldLeft(1)(_ * _)

//implementation of foldleft and reduceleft
sealed abstract class List[T]:
    def reduceLeft(op: (T, T) => T): T = this match
        case Nil => throw IllegalOperationException(”Nil.reduceLeft”)
        case x :: xs => xs.foldLeft(x)(op)
    def foldLeft[U](z: U)(op: (U, T) => U): U = this match
        case Nil => z
        case x :: xs => xs.foldLeft(op(z, x))(op)

//Foldright and reduceright produce a tree that lean right instead of leaning left
List(x1, ..., x{n-1}, xn).reduceRight(op)
==
x1.op(x2.op( ... x{n-1}.op(xn) ... ))
List(x1, ..., xn).foldRight(z)(op )
==
x1.op(x2.op( ... xn.op(z) ... ))
//Implementation
def reduceRight(op: (T, T) => T): T = this match
    case Nil => throw UnsupportedOperationException(”Nil.reduceRight”)
    case x :: Nil => x
    case x :: xs => op(x, xs.reduceRight(op))
def foldRight[U](z: U)(op: (T, U) => U): U = this match
    case Nil => z
    case x :: xs => op(x, xs.foldRight(z)(op))

//Derived functions using reduction
def concat[T](xs: List[T], ys: List[T]): List[T] =
    xs.foldRight(ys)(_ :: _)
def reverse[a](xs: List[T]): List[T] =
    xs.foldLeft[List[T]](Nil)((xs, x) => x :: xs)
def mapFun[T, U](xs: List[T], f: T => U): List[U] =
    xs.foldRight[List[U]](Nil)((y, ys) => f(y) :: ys)
def lengthFun[T](xs: List[T]): Int =
    xs.foldRight(0)((y, n) => n + 1)
```

### Pure Data
- can pull out all the case classes using an import
```scala
import Expr.*

sealed abstract class Expr
object Expr:
    case class Var(s: String) extends Expr
    case class Number(n: Int) extends Expr
    case class Sum(e1: Expr, e2: Expr) extends Expr
    case class Prod(e1: Expr, e2: Expr) extends Expr
// Short hand for above ADT (Algebraic data type)
enum Expr:
    case Var(s: String)
    case Number(n: Int)
    case Sum(e1: Expr, e2: Expr)
    case Prod(e1: Expr, e2: Expr)
// Can put case on the same line
enum Color:
    case Red, Green, Blue
// can define methods and take parameters (explicit extends)
enum Direction(val dx: Int, val dy: Int):
    case Right extends Direction( 1, 0)
    case Up extends Direction( 0, 1)
    case Left extends Direction(-1, 0)
    case Down extends Direction( 0, -1)
    def leftTurn = Direction.values((ordinal + 1) % 4)
    end Direction
val r = Direction.Right
val u = x.leftTurn // u = Up
val v = (u.dx, u.dy) // v = (1, 0)
//Useful for domain modelling
enum PaymentMethod:
    case CreditCard(kind: CardKind, holder: String, number: Long, expires: Date)
    case PayPal(email: String)
    case Cash
enum CardKind:
    case Visa, Mastercard, Amex
```
### Subtyping and Generics (Polymorphism)
#### Type bounds
```scala
//Upper bound (S have to be subtype of T)
def assertAllPos[S <: IntSet](r: S): S = ...
//Lower bound (S have to be supertype of NonEmpty)
[S >: NonEmpty]
// Mixed bound
[S >: NonEmpty <: IntSet]
//covariant (Accept mutations)
NonEmpty[] <: IntSet[]
List[NonEmpty] <: List[IntSet]
```
Liskov substitution principle:

>if A <: B then everything that can do with B can be able to do with A
```scala
val a: Array[NonEmpty] = Array(NonEmpty(1, Empty(), Empty()))
val b: Array[IntSet]=a
b(0) = Empty()
val s: NonEmpty = a(0)
//we cannot assign a to b due to type error in line 2
```
### Variance
```scala
Suppose A <: B and C[T] is a parameterized type:
C[A] <: C[B]                C is covariant      C[+A]
C[A] >: C[B]                C is contravariant  C[-A]
neither C[A] nor C[B]       C is nonvariant     C[A]
is a subtype of the other 
//Typing rule for functions
If A2 <: A1 and B1 <: B2, then
A1 => B1 <: A2 => B2
covariant for result, contravariant for args
package scala
trait Function1[-T, +U]:
def apply(x: T): U
```
- covariant type parameters can only appear in method results.
- contravariant type parameters can only appear in method parameters.
- invariant type parameters can appear anywhere
```scala
trait List[+T]:
    def isEmpty = this match
        case Nil => true
        case _ => false
    override def toString =
        def recur(prefix: String, xs: List[T]): String = xs match
            case x :: xs1 => s”$prefix$x${recur(”, ”, xs1)}”
            case Nil => ”)”
        recur(”List(”, this))

case class ::[+T](head: T, tail: List[T]) extends List[T]
case object Nil extends List[Nothing]
extension [T](x: T) def :: (xs: List[T]): List[T] = ::(x, xs)
object List:
    def apply() = Nil
    def apply[T](x: T) = x :: Nil
    def apply[T](x1: T, x2: T) = x1 :: x2 :: Nil
// Not Work, T cannot be method parameter
def prepend(elem: T): List[T] = ::(elem, this)
//variance checking works when we use a lower bound
def prepend [U >: T] (elem: U): List[U] = ::(elem, this)
//side step the problem to use extension method without variance
extension [T](x: T):
    def :: (xs: List[T]): List[T] = ::(x, xs)
```
### Tuple and Generic Methods
```scala
//tuple
case class Tuple2[+T1, +T2](_1: T1, _2: T2):
    override def toString = ”(” + _1 + ”,” + _2 +”)”
//merge sort O(nlogn)
def msort[T](xs: List[T])(lt: (T, T) => Boolean) =
    val n = xs.length / 2
    if n == 0 then xs
    else
    def merge[T](xs: List[T], ys: List[T]) = (xs, ys) match
        case (Nil, ys) => ys
        case (xs, Nil) => xs
        case (x :: xs1, y :: ys1) =>
            if lt(x, y) then x :: merge(xs1, ys)
            else y :: merge(xs, ys1)
    val (fst, snd) = xs.splitAt(n)
    merge(msort(fst)(lt), msort(snd)(lt))

extension [A](xs: List[A])
    def splitAt(n: Int): (List[A], List[A]) =
        (xs.take(n), xs.drop(n))

```
### Structural Induction
The principle of structural induction is analogous to natural induction:

To prove a property P(xs) for all lists xs,
1. show that P(Nil) holds (base case),
1. for a list xs and some element x, show the induction step:
if P(xs) holds, then P(x :: xs) also holds.

```scala
show that (xs ::: ys) ::: zs = xs ::: (ys ::: zs) using 
Nil ::: ys = ys // 1st clause
(x :: xs1) ::: ys = x :: (xs1 ::: ys) // 2nd clause

Base case: Nil
For the left-hand side we have:
(Nil ::: ys) ::: zs
= ys ::: zs // by 1st clause of :::
For the right-hand side, we have:
Nil ::: (ys ::: zs)
= ys ::: zs // by 1st clause of :::
This case is therefore established.

Induction step: x :: xs
For the left-hand side, we have:
((x :: xs) ::: ys) ::: zs
= (x :: (xs ::: ys)) ::: zs // by 2nd clause of :::
= x :: ((xs ::: ys) ::: zs) // by 2nd clause of :::
= x :: (xs ::: (ys ::: zs)) // by induction hypothesis
For the right hand side we have:
(x :: xs) ::: (ys ::: zs)
= x :: (xs ::: (ys ::: zs)) // by 2nd clause of :::
So this case (and with it, the property) is established.
```
```scala
//Harder
show that xs.reverse.reverse = xs using
Nil.reverse = Nil // 1st clause
(x :: xs).reverse = xs.reverse ::: x :: Nil // 2nd clause

Base case: Nil
Nil.reverse.reverse
= Nil.reverse // by 1st clause of reverse
= Nil // by 1st clause of reverse

Induction step:
For the left-hand side, we have:
(x :: xs).reverse.reverse
= (xs.reverse ::: x :: Nil).reverse // by 2nd clause of reverse
//stuck so go prove this:
(xs.reverse ::: x :: Nil).reverse = x :: xs.reverse.reverse
//Generalized:
(ys ::: x :: Nil).reverse = x :: ys.reverse

Base case: Nil
(Nil ::: x :: Nil).reverse // to show: = x :: Nil.reverse
= (x :: Nil).reverse // by 1st clause of :::
= (x :: Nil).reverse // by definition of List
= Nil ::: (x :: Nil) // by 2nd clause of reverse
= x :: Nil // by 1st clause of :::
= x :: Nil.reverse // by 1st clause of reverse

Induction step:
((y :: ys) ::: x :: Nil).reverse // to show: = x :: (y :: ys).reverse
= (y :: (ys ::: x :: Nil)).reverse // by 2nd clause of :::
= (ys ::: x :: Nil).reverse ::: y :: Nil // by 2nd clause of reverse
= (x :: ys.reverse) ::: y :: Nil // by the induction hypothesis
= x :: (ys.reverse ::: y :: Nil) // by 1st clause of :::
= x :: (y :: ys).reverse // by 2nd clause of reverse
This establishes the auxiliary equation, and with it the main proposition.
```
#### Extra exercise, should finish before midterm
```scala
Prove the following distribution law for map over concatenation.
For any lists xs, ys, function f:
    (xs ::: ys).map(f) = xs.map(f) ::: ys.map(f)
You will need the clauses of ::: as well as the following clauses for map:
    Nil.map(f) = Nil
    (x :: xs).map(f) = f(x) :: xs.map(f)
```

### Other Collections
#### Vectors
```scala
val nums = Vector(1, 2, 3, -88)
val people = Vector(”Bob”, ”James”, ”Peter”)

x +: xs Create a new vector with leading element x, followed
by all elements of xs.
xs :+ x Create a new vector with trailing element x, preceded
by all elements of xs.
```
#### Arrays and Strings
```scala
val xs: Array[Int] = Array(1, 2, 3)
xs.map(x => 2 * x)
val ys: String = ”Hello world!”
ys.filter(_.isUpper)
```
#### Ranges
```scala
to (inclusive), until (exclusive), by (to determine step value):
val r: Range = 1 until 5
val s: Range = 1 to 5
1 to 10 by 3
6 to 1 by -2
```
### Applications
 list all combinations of numbers x and y where x is drawn from 1..M and y is drawn from 1..N:

```scala
(1 to M).flatMap(x => (1 to N).map(y => (x, y)))

def scalarProduct(xs: Vector[Double], ys: Vector[Double]): Double =
    xs.zip(ys).map(_ * _).sum
    
def isPrime(n: Int): Boolean =
    (2 to n - 1).forall(d => n % d != 0)
```

## Appendices
1.1 List functions
```

xs.length The number of elements of xs.

xs.last The list’s last element, exception if xs is empty.

xs.init A list consisting of all elements of xs except the
last one, exception if xs is empty.

xs.take(n) A list consisting of the first n elements of xs, or xs
itself if it is shorter than n.

xs.drop(n) The rest of the collection after taking n elements.

xs(n) (or, written out, xs.apply(n)). The element of xs
at index n.

```
1.2 Higher Order list functions
```
xs.filterNot(p) Same as xs.filter(x => !p(x)); The list consisting of those elements of xs that do not satisfy the
predicate p.

xs.partition(p) Same as (xs.filter(p), xs.filterNot(p)), but
computed in a single traversal of the list xs.

xs.takeWhile(p) The longest prefx of list xs consisting of elements
that all satisfy the predicate p.

xs.dropWhile(p) The remainder of the list xs after any leading elements satisfying p have been removed.

xs.span(p) Same as (xs.takeWhile(p), xs.dropWhile(p)) but
computed in a single traversal of the list xs.

```
1.3 Other Collections
```
xs.exists(p) true if there is an element x of xs such that p(x) holds, false otherwise.

xs.forall(p) true if p(x) holds for all elements x of xs, false otherwise.

xs.zip(ys) A sequence of pairs drawn from corresponding elements
of sequences xs and ys.

xs.unzip Splits a sequence of pairs xs into two sequences consisting of the frst, respectively second halves of all pairs.

xs.flatMap(f) Applies collection-valued function f to all elements of

xs and concatenates the results

xs.sum The sum of all elements of this numeric collection.

xs.product The product of all elements of this numeric collection

xs.max The maximum of all elements of this collection (an
Ordering must exist)

xs.min The minimum of all elements of this collection
```