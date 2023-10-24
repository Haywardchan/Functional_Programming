## PastPaper Summary
### List functions
```scala
def reverse[A](xs: List[A]) = xs.foldLeft(List[A]())((r,c)=>c::r)

def scanLeft[A, B >: A](xs: List[A])(z: B)(op: (B, B) => B): List[B]=
    reverse(xs.foldLeft(z, List(z)){
        case ((acc, accList), curr) => 
            val res = op(acc, curr)
            (res, res :: accList)
    }._2)
```
```scala
def insert(elem: Int, list: List[Int]):List[Int] = list match
    case x :: xs if x < elem => x :: insert(elem, xs)
    case _ => elem :: list

def findKLargestElem(k: Int)(list: List[Int]): List[Int] = 
    list.foldLeft(List.empty[Int]){
        (acc: List[Int], elem: Int) =>
            val newAcc = insert(elem, acc)

            if (newAcc.size > k) newAcc.tail else newAcc
    }
```
```scala
def tails(ls: List[Int]): List[List[Int]] =
    ls :: (ls match {
        case x::xs => tails(xs)
        case Nil => Nil
    })

def longest[A](ls: List[A]): Int =
    ls.foldLeft((Option.empty[A], 0, 0)){
        case ((last, curr, max), x) =>
            val last2 = Some(x)
            val cur2 = if (last2 == last) cur+1 else 1
            (last2, cur2, if (cur2 > max) cur2 else max)
    }._3
```
```scala
def lists(n: Int): Generator[List[Int]] =
    if (n <= 0)
        single(Nil)
    else
        for {
            head <- integers
            tail <- lists(n - 1)
        } yield head :: tail

def listsUpTo(limit: Int): Generator[List[Int]] =
    for {
        n <- atMost(limit)
        list <- lists(n)
    } yield list

def sortedLists(n: Int, minimum: Int): Generator[List[Int]] =
    if (n <= 0)
        single(Nil)
    else
    for {
        head <- atLeast(minimum)
        tail <- sortedLists(n - 1, head)
    } yield head :: tail
```
```scala
def takeWhileStrictlyIncreasing1(list: List[Int]): List[Int] = list match
    case Nil => Nil
    case x :: Nil => list
    case x1 :: x2 :: xs => if x1 < x2 then x1 :: takeWhileStrictlyIncreasing1(x2 ::
    xs) else List(x1)

def increasingSequences(list: List[Int]): List[List[Int]] = list match
    case Nil => Nil
    case _ =>
    val prefix = takeWhileStrictlyIncreasing(list)
    prefix :: increasingSequences(list.drop(prefix.length))
```
### For Comprehension
```scala
final case class Square(x: Int, y: INt)

def computeFlips(square: Square): List[Square] = {
    List(-1, 0, -1).flatMap{ i =>
        List(-1, 0, 1).filter{ j =>
            i!=0||j!=0
        }.flatMap{ j=>
            computeFlipsInDirection(square, i, j)
        }
    }
}
def computeFlips(square: Square): List[Square] = {
    for {
        i <- List(-1, 0, 1)
        j <- List(-1, 0, 1)
        if i!=0 || j!=0
        flip <- computeFlipsInDirection(square.x, square.y, i, j)
    } yield flip
}
```
```scala
def generatePassedExams(students: List[Student], courses: List[Course]): List[(String, String, Double)] = {
    for {
        s <- students
        e <- s.exams
        if e.grade > 2
        c <- courses
        if e.courseId == c.id
    } yield (s.name, c.name, e.grade)
}

def generatePassedExams(students: List[Student], courses: List[Course]): List[(String, String, Double)] = {
    students.flatMap{ s =>
        s.exams
        .filter(_.grade > 2)
        .flatMap{ e =>
            courses
            .filter(c=>e.courseId==c.id)
            .map(c=>s.name, c.name, e.grade)
        }
    }
}
```
### Graph
```scala
//reachable exactly n steps
def reachable(n: Int, init: Set[Node], edges: List[Edge]): Set[Node] = n match
    case 0 => init
    case _ => 
        val next = init.flatMap(node => edges.filter(_.from == node).map(_.to))
        reachable(n-1, next, edges)
//all cycles of 3
def cycles3(nodes: Set[Node], edges: List[Edge]): Set[Node] = 
    nodes.filter(node => reachable(3, Set(Node), edges).contains(nodes))

def nodes(g: Graph): Set[Node] =
    g.flatMap((x, y) => x :: y :: Nil)

def pathsFrom(g: Graph, n: Node, l: Int): Set[List[Node]] =
    def rec(l: Int, acc: Set[List[Node]]): Set[List[Node]] =
        if l == 0 then
            acc
        else
            rec(l - 1, acc.flatMap(p =>
                val h = p.head
                g.filter(_._1 == h).map(_._2 :: p)
            ))
    rec(l, Set(n :: Nil)).map(_.reverse)

def conjoinedCycles(g: Graph, l: Int): Set[Node] =
    nodes(g).filter(n => pathsFrom(g, n, l).count(_.last == n) > 1)
```
### Tree
```scala
def size: Int =
    this match {
        case Empty() => 0
        case Layer(_, next) => 1 + 2 * next.size
    }

def map[B](f: A => B): Perfect[B] =
    this match {
        case Empty() => Empty()
        case Layer(elem, next) => Layer(f(elem), next.map { case (a, b) => (f(a), f(b)) })
    }

def toList: List[A] =
    this match {
        case Empty() => Nil
        case Layer(elem, next) => elem :: next.toList.flatMap { case (a, b) => a :: b :: Nil }
    }

trait Tree[T]:
def height: Int = this match
    case EmptyTree(_) => 0
    case Node(left, _, right, _) => Math.max(left.height, right.height) + 1

def isBalanced: Boolean = this match
    case EmptyTree(_) => true
    case Node(left, _, right, _) => left.isBalanced && right.isBalanced &&
    Math.abs(left.height - right.height) <= 1
```
### pattern matching
```scala
def eval1(e: Expr, context: Map[Var, Int]): Int = e match
    case Var(name) => context.get(Var(name)) match
        case Some(n) => n
        case _ => throw UnknownVarException(name)
    case Op(name, args) =>
        val nargs = args.map(eval1(_, context))
        name match
            case "*" => nargs.foldLeft(1)(_ * _)
            case "+" => nargs.foldLeft(0)(_ + _)
            case _ => throw UnknownOpException(name)
```
### Generators
```scala
trait Generator[+T]:
def generate(): T
def map[S](f: T => S) = new Generator[S]:
def generate() = f(Generator.this.generate())
def flatMap[S](f: T => Generator[S]) = new Generator[S]:
def generate() = f(Generator.this.generate()).generate()

val booleans = for x <- integers yield x>0
val booleans = integers.map(x => x > 0)
val booleans = new Generator[Boolean]:
def generate() = ((x: Int) => x > 0)(integers.generate())
val booleans = new Generator[Boolean]:
def generate() = integers.generate() > 0

    def pairs[T, U](t: Generator[T], u: Generator[U]) = t.flatMap(
    x => u.map(y => (x, y)))
    def pairs[T, U](t: Generator[T], u: Generator[U]) = t.flatMap(
    x => new Generator[(T, U)] { def generate() = (x, u.generate()) })
def pairs[T, U](t: Generator[T], u: Generator[U]) = new Generator[(T, U)]:
    def generate() = (new Generator[(T, U)]:
    def generate() = (t.generate(), u.generate())
).generate()
def pairs[T, U](t: Generator[T], u: Generator[U]) = new Generator[(T, U)]:
    def generate() = (t.generate(), u.generate())

def single[T](x: T): Generator[T] = new Generator[T]:
    def generate() = x
def range(lo: Int, hi: Int): Generator[Int] =
    for x <- integers yield lo + x.abs % (hi - lo)
def oneOf[T](xs: T*): Generator[T] =
    for idx <- range(0, xs.length) yield xs(idx)

def lists: Generator[List[Int]] =
    for
    isEmpty <- booleans
    list <- if isEmpty then emptyLists else nonEmptyLists
    yield list
def emptyLists = single(Nil)
def nonEmptyLists =
    for
    head <- integers
    tail <- lists
    yield head :: tail

def leaves: Generator[Leaf] = for {
    x <- integers
    } yield Leaf(x)
def inners: Generator[Inner] = for {
    l <- trees
    r <- trees
    } yield Inner(l, r)
def trees: Generator[Tree] = for {
    cutoff <- booleans
    tree <- if (cutoff) leafs else inners
    } yield tree

def test[T](g: Generator[T], numTimes: Int = 100)
(test: T => Boolean): Unit =
    for i <- 0 until numTimes do
    val value = g.generate()
    assert(test(value), s”test failed for $value”)
    println(s”passed $numTimes tests”)

extension [A, B, C](f: A => B)
    def andThen(g: B => C): A => C =
    x => g(f(x))

extension [T](xo: Option[T])
    def flatMap[U](f: T => Option[U]): Option[U] = xo match
        case Some(x) => f(x)
        case None => None

def validatedInput(): String =
    try getInput()
    catch
        case BadInput(msg) => println(msg); validatedInput()
        case ex: Exception => println(”fatal error; aborting”); throw ex

object Try:
    def apply[T](expr: => T): Try[T] =
        try Success(expr)
        catch case NonFatal(ex) => Failure(ex)

extension [T](xt: Try[T])
def flatMap[U](f: T => Try[U]): Try[U] = xt match
    case Success(x) => try f(x) catch case NonFatal(ex) => Failure(ex)
    case fail: Failure => fail
def map[U](f: T => U): Try[U] = xt match
    case Success(x) => Try(f(x))
    case fail: Failure => fail
```
### Structural Induction on trees
```scala
Base Case: Empty()
Inductive step: NonEmpty(x, l, r)
NonEmpty(y, l, r)
z=x then z=y
NonEmpty(z, l, r) where z<y<x
NonEmpty(z, l, r) where y<z<x
NonEmpty(z, l, r) where y<x<z
```
### Lazy List
```scala
def lazyRange(lo: Int, hi: Int): LazyList[Int] =
    if lo >= hi then LazyList.empty
    else LazyList.cons(lo, lazyRange(lo + 1, hi))

object TailLazyList:
        def cons[T](hd: T, tl: => TailLazyList[T]) = new TailLazyList[T]:
        def isEmpty = false
        def head = hd
        def tail = tl
        override def toString = ”LazyList(” + hd + ”, ?)”
    val empty = new TailLazyList[Nothing]:
        def isEmpty = true
        def head = throw NoSuchElementException(”empty.head”)
        def tail = throw NoSuchElementException(”empty.tail”)
        override def toString = ”LazyList()”

lazyRange(1000, 10000).filter(isPrime).apply(1)
--> (if 1000 >= 10000 then empty // by expanding lazyRange
else cons(1000, lazyRange(1000 + 1, 10000))
.filter(isPrime).apply(1)
--> cons(1000, lazyRange(1000 + 1, 10000)) // by evaluating if
.filter(isPrime).apply(1)
```
### Infinite List
```scala
def from(n: Int): LazyList[Int] = n #:: from(n + 1)

def sieve(s: LazyList[Int]): LazyList[Int] =
    s.head #:: sieve(s.tail.filter(_ % s.head != 0))
val primes = sieve(from(2))
```

```scala
def queens(n: Int) =
def placeQueens(k: Int): Set[List[Int]] =
if k == 0 then Set(Nil)
else
for
queens <- placeQueens(k - 1)
col <- 0 until n
if isSafe(col, queens)
yield col :: queens
placeQueens(n)
```