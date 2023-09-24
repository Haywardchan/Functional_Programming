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

