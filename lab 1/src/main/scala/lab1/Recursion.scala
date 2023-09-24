package lab1

import scala.annotation.tailrec

object Recursion:

  def main(args: Array[String]): Unit =
    println("Pascal's Triangle")
    for row <- 0 to 10 do
      for col <- 0 to row do
        print(s"${pascal(col, row)} ")
      println()

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = 
    if(c==0) 1
    else if(r==0) 0
    else pascal(c,r-1)+pascal(c-1,r-1)


  /**
   * Exercise 2
   */
  // def balance(chars: List[Char]): Boolean = 
  //   @tailrec
  //   def rec(xs:List[Char], bal:Int): Boolean =
  //     if (bal < 0 || xs.isEmpty) then false
  //     else if (xs.isEmpty && bal==0) then true 
  //     if xs.head=='(' then rec(xs.tail, bal+1)
  //     else if xs.head==')' then rec(xs.tail, bal-1)
  //     else rec(xs.tail, bal)
  //   rec(chars, 0)

  def balance(chars: List[Char]): Boolean =
    @tailrec
    def rec(chars: List[Char], acc: Int): Boolean = chars match
      case _ if acc < 0 => false
      case '(' :: tail => rec(tail, acc + 1)
      case ')' :: tail => rec(tail, acc - 1)
      case _ :: tail => rec(tail, acc)
      case Nil if acc == 0 => true
      case Nil => false

    rec(chars, 0)
  /*
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = 
    if money==0 then 1
    else if money < 0 then 0 
    else coins match
      case coin :: tail => countChange(money-coin, coins)+countChange(money, tail)
      case Nil => 0