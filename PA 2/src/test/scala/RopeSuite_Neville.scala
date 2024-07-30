package ass2

import ass2.Rope.{Concat, Repeat, Slice}

// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
class RopeSuite_Neville extends munit.FunSuite {
  extension (s: String)
    def rope: Rope = Rope(s)

  val X = "x" * 10000000//should have 1 more zero
  // val X = "x" * 2147483647//should have 1 more zero

  test("apply - basic cases") {
    assertEquals("hello".rope(0), 'h')
    assertEquals("hello".rope(1), 'e')
    assertEquals("hello".rope(2), 'l')
    assertEquals("hello".rope(3), 'l')
    assertEquals("hello".rope(4), 'o')
  }

  test("apply - neville cases") {
    assertEquals(("foo".rope + "bar".rope).apply(3), 'b')
    assertEquals(("foo".rope * 3).apply(4), 'o')
    assertEquals(("foo".rope * 3).apply(8), 'o')
    assertEquals(("abc".rope * 3).slice(5, 7).apply(1), "abcabcabc".slice(5, 7).charAt(1))
  }

  test("apply - exception") {
    intercept[IndexOutOfBoundsException] {
      ("foo".rope * 3).apply(9)
      ("foo".rope).apply(3)
      ("foo".rope + "bar".rope).apply(6)
    }
  }

  test("length - basic cases") {
    assertEquals("hello".rope.length, 5)
    assertEquals(("foo".rope + "bar".rope).length, 6)
    assertEquals(("foo".rope * 3).length, 9)
    assertEquals(("foo".rope * 3).slice(1, 4).length, 3)
  }

  test("length - neville cases") {
    assertEquals("".rope.length, 0)
  }

  test("indexOf - basic cases") {
    assertEquals("hello".rope.indexOf("hello", 0), 0)
    assertEquals("hello".rope.indexOf("hello", 1), -1) // NOT FOUND
    assertEquals("hello".rope.indexOf("l", 0), 2)
    assertEquals("hello".rope.indexOf("l", 3), 3)
    assertEquals("hello".rope.indexOf("o", 4), 4)
  }

  test("indexOf - concat tests") {
    assertEquals(Concat("xxxxxxnevi".rope, "llexxxxxx".rope).indexOf("neville", 2), 6)
    assertEquals(Concat("xxxxxxnevi".rope, "llexxxxxx".rope).indexOf("nevi", 3), 6)
    assertEquals(Concat("xxxxxxnevi".rope, "llexxxxxx".rope).indexOf("lex", 11), 11)
  }

  test("indexOf - slice tests") {
    assertEquals(Slice("xxxnevillexxx".rope, 0, 2).indexOf("neville", 0), -1)
    assertEquals(Slice("xxxnevillexxx".rope, 0, 10).indexOf("neville", 0), 3)
    assertEquals(Slice("xxxnevillexxx".rope, 0, 10).indexOf("neville", 2), 3)
    assertEquals(Slice("xxxnevillexxx".rope, 4, 10).indexOf("neville", 2), -1)
  }

  test("indexOf - slice concat tests") {
    assertEquals(Slice("hello".rope+"world".rope,0, 4).indexOf("hell", 0), 0)
    assertEquals(Slice("hello".rope+"world".rope,5, 8).indexOf("or", 1), 6)
    assertEquals(Slice("hello".rope+"world".rope,2, 8).indexOf("owo", 0), 4)
    assertEquals(Slice("xxxnevi".rope+"llexxx".rope, 0, 2).indexOf("neville", 0), -1)
    assertEquals(Slice("xxxnevillex".rope+"xx".rope, 0, 10).indexOf("neville", 0), 3)
    assertEquals(Slice("xxxn".rope+"evillexxx".rope, 0, 10).indexOf("neville", 2), 3)
    assertEquals(Slice("xxxnevill".rope+"exxx".rope, 4, 10).indexOf("neville", 2), -1)
  }

  // test("indexOf - slice slice tests") {
  //   assertEquals(Slice(Slice("helloworld".rope,0, 4)).indexOf("hell", 0), 0)
  //   assertEquals(Slice("helloworld".rope,5, 8).indexOf("or", 1), 6)
  //   assertEquals(Slice("helloworld".rope,2, 8).indexOf("owo", 0), 4)
  //   assertEquals(Slice("xxxnevillexxx".rope, 0, 2).indexOf("neville", 0), -1)
  //   assertEquals(Slice("xxxnevillexxx".rope, 0, 10).indexOf("neville", 0), 3)
  //   assertEquals(Slice("xxxnevillexxx".rope, 0, 10).indexOf("neville", 2), 3)
  //   assertEquals(Slice("xxxnevillexxx".rope, 4, 10).indexOf("neville", 2), -1)
  // }

  test("indexOf - repeat tests") {
    assertEquals(Repeat("neville".rope, 10000000).indexOf("vi", 0), Repeat("neville".rope, 10000000).toString().indexOf("vi", 0))
    assertEquals(Repeat("neville".rope, 10000000).indexOf("en", 0), Repeat("neville".rope, 10000000).toString().indexOf("en", 0))
    assertEquals(Repeat("neville".rope, 10000000).indexOf("llen", 3), Repeat("neville".rope, 10000000).toString().indexOf("llen", 3))
    assertEquals(Repeat("neville".rope, 10000000).indexOf("evillenevillen", 1), Repeat("neville".rope, 10000000).toString().indexOf("evillenevillen", 1))
    assertEquals(Repeat("neville".rope, 10000000).indexOf("evillenevillen", 2), Repeat("neville".rope, 10000000).toString().indexOf("evillenevillen", 2))
    assertEquals(Repeat("neville".rope, 100).indexOf("neville", 93), Repeat("neville".rope, 100).toString().indexOf("neville", 93))
    assertEquals(Repeat("abc".rope, 100).indexOf("bcabca", 2), Repeat("abc".rope, 100).toString().indexOf("bcabca", 2))
    assertEquals(Repeat("abc".rope, 10).indexOf("bcabcabca", 21), Repeat("abc".rope, 10).toString().indexOf("bcabcabca", 21))
  }

  test("toString - basic cases") {
    assertEquals("hello".rope.toString, "hello")
    assertEquals(("foo".rope + "bar".rope).toString, "foobar")
    assertEquals(("foo".rope * 3).toString, "foofoofoo")
    assertEquals(("foo".rope * 3).slice(1, 4).toString, "oof")
  }

  test("toString - slice with concat") {
    assertEquals(Slice(Concat(("catch").rope, ("me" + X).rope), 0, 7).toString(), "catchme")
  }

  test("toString - slice with concat 2") {
    assertEquals(Slice(Concat(("catch").rope, ("me" + X).rope), 5, 7).toString(), "me")
  }

  test("toString - slice with slice") {
    assertEquals(Slice(Slice(("catchme" + X).rope, 0, 7), 0, 100000000).toString(), "catchme")
  }

  test("toString - slice with repeat") {
    assertEquals(Slice(Repeat("catchme".rope, 100000000), 0, 10).toString(), "catchmecatchme".slice(0, 10))
  }

  test("toString - slice with repeat 2") {
    assertEquals(Slice(Repeat("catchme".rope, 100000000), 5, 8).toString(), "catchmecatchme".slice(5, 8))
  }

  test("toString - slice with repeat 3") {
    assertEquals(Slice(Repeat("catchme".rope, 100000000), 0, 3).toString(), "catchme".slice(0, 3))
  }

  test("toString - slice with repeat 4") {
    assertEquals(Slice(Repeat("abc".rope, 100000000), 99, 105).toString(), "abcabc")
  }

  test("toString - slice with repeat 5") {
    assertEquals(Slice(Repeat("abc".rope, 100000000), 100, 106).toString(), "abcabcabc".slice(1, 7))
  }

  test("toString - slice with repeat 6") {
    assertEquals(Slice(Repeat("abc".rope, 100000000), 3*(100000000-1), 3*100000000).toString(), ("abc" * 100000000).slice(3*(100000000-1), 3*100000000))
  }

  test("toString - slice with repeat 7") {
    assertEquals(Slice(Repeat("abc".rope, 100000000), 3 * 100000000 - 3, 3 * 100000000).toString(), ("abc" * 100000000).slice(3 * 100000000 - 3, 3 * 100000000))
  }

  test("insert - basic cases") {
    assertEquals("hello".rope.insert("world".rope, 0).toString, "worldhello")
    assertEquals("hello".rope.insert("world".rope, 1).toString, "hworldello")
    assertEquals("hello".rope.insert("world".rope, 5).toString, "helloworld")
  }

  test("delete - basic cases") {
    assertEquals("hello".rope.delete(0, 0).toString, "hello")
    assertEquals("hello".rope.delete(0, 1).toString, "ello")
    assertEquals("hello".rope.delete(0, 2).toString, "llo")
    assertEquals("hello".rope.delete(0, 3).toString, "lo")
    assertEquals("hello".rope.delete(0, 4).toString, "o")
    assertEquals("hello".rope.delete(0, 5).toString, "")
    assertEquals("hello".rope.delete(1, 1).toString, "hello")
    assertEquals("hello".rope.delete(1, 2).toString, "hllo")
    assertEquals("hello".rope.delete(1, 3).toString, "hlo")
    assertEquals("hello".rope.delete(1, 4).toString, "ho")
    assertEquals("hello".rope.delete(1, 5).toString, "h")
    assertEquals("hello".rope.delete(2, 2).toString, "hello")
    assertEquals("hello".rope.delete(2, 3).toString, "helo")
    assertEquals("hello".rope.delete(2, 4).toString, "heo")
    assertEquals("hello".rope.delete(3, 3).toString, "hello")
    assertEquals("hello".rope.delete(3, 4).toString, "helo")
    assertEquals("hello".rope.delete(4, 4).toString, "hello")
  }

  test("split - basic cases") {
    assertEquals("hello".rope.split("hello").map(_.toString), List("", ""))
    assertEquals("hello".rope.split("h").map(_.toString), List("", "ello"))
    assertEquals("hello".rope.split("e").map(_.toString), List("h", "llo"))
    assertEquals("hello".rope.split("l").map(_.toString), List("he", "", "o"))
    assertEquals("hello".rope.split("o").map(_.toString), List("hell", ""))
    assertEquals("hello".rope.split("").map(_.toString), "hello".split("").toList.map(_.toString))
  }

  test("split concat - hayward's cases") {
    assertEquals(("hello".rope+"world".rope).split("hello").map(_.toString), List("", "world"))
    assertEquals(("hello".rope+"world".rope).split("owo").map(_.toString), List("hell", "rld"))
    assertEquals(("hello".rope+"world".rope).split("h").map(_.toString), List("", "elloworld"))
    assertEquals(("hello".rope+"world".rope).split("o").map(_.toString), List("hell", "w", "rld"))
    assertEquals(("hello".rope+"world".rope).split("d").map(_.toString), List("helloworl", ""))
    assertEquals(("hello".rope+"world".rope).split("").map(_.toString), "helloworld".split("").toList.map(_.toString))
  }

  test("replace - basic cases") {
    // Replacement on leaf nodes.
    val naisbitt = "We're drowning in information and starving for knowledge.".rope
    assertEquals(
      naisbitt.replace("information", "information superhighway").toString,
      "We're drowning in information superhighway and starving for knowledge."
    )
    assertEquals(
      naisbitt.replace("information", "information superhighway")
              .replace("knowledge", "wisdom")
              .toString,
      "We're drowning in information superhighway and starving for wisdom."
    )
    // Replacement on concat nodes.
    val shakesphare = "All the world's a stage, ".rope +
      "and all the men and women merely players.".rope
    assertEquals(
      shakesphare.replace("world's", "world is").toString,
      "All the world is a stage, and all the men and women merely players."
    )
    assertEquals(
      shakesphare.replace("stage, and", "stage and").toString,
      "All the world's a stage and all the men and women merely players."
    )
  }

  test("replace - empty text case"){
    assertEquals(
      "abc".rope.replace("", "x").toString,
      "xaxbxcx"
    )
  }

  test("duplicate - basic cases") {
    assertEquals("hello".rope.duplicate(0, 2, 0).toString, "llo")
    assertEquals("hello".rope.duplicate(0, 2, 1).toString, "hello")
    assertEquals("hello".rope.duplicate(0, 2, 2).toString, "hehello")
  }

  test("simplify - basic cases") {
    assertEquals("hello".rope.simplify, Rope.Leaf("hello"))
    assertEquals(("foo".rope + "bar".rope).simplify, Rope.Leaf("foo") + Rope.Leaf("bar"))
    assertEquals(("foo".rope * 0).simplify, Rope.empty)
    assertEquals(("foo".rope * 1).simplify, Rope.Leaf("foo"))
    assertEquals("dragonslayer".rope.slice(0, 7).simplify, Rope.Leaf("dragons"))
  }

  test("simplify - leaf"){
    assertEquals("hello".rope.simplify, Rope.Leaf("hello"))
  }

  test("simplify - concat") {
    assertEquals(("foo".rope + Rope.empty).simplify, Rope.Leaf("foo"))
  }

  test("simplify - repeat") {
    assertEquals(("foo".rope * 3).simplify, Rope.Leaf("foo") + Rope.Leaf("foo") + Rope.Leaf("foo"))
  }

  test("simplify - slice") {
    assertEquals("hello".rope.slice(0, 1).simplify, Rope.Leaf("h"))
    assertEquals(("foo".rope + "bar".rope).slice(0, 1).simplify, Rope.Leaf("f"))
    assertEquals(("foo".rope + "bar".rope).slice(3, 4).simplify, Rope.Leaf("b"))
    assertEquals(("foo".rope + "bar".rope).slice(1, 4).simplify, Rope.Leaf("oo") + Rope.Leaf("b"))
    assertEquals(("foo".rope + "bar".rope).slice(2, 4).simplify, Rope.Leaf("o") + Rope.Leaf("b"))
    assertEquals(("o".rope + "b".rope).slice(1,2).simplify, Rope.Leaf("b"))
    assertEquals(("foo".rope + "bar".rope).slice(2,4).slice(1,2).simplify, Rope.Leaf("b"))
    assertEquals(Slice(Slice("foo".rope + "bar".rope, 2, 4), 0, 2).simplify, Rope.Leaf("o") + Rope.Leaf("b"))
    assertEquals(("foo".rope * 9).slice(2, 4).simplify, Rope.Leaf("o") + Rope.Leaf("f"))
    assertEquals(("lol".rope + "lol".rope).simplify, "lol".rope + "lol".rope)
    assertEquals(Repeat(Repeat("lol".rope, 0), 1000).simplify, "".rope)
    assertEquals(("hi".rope + "".rope).simplify, "hi".rope)
    assertEquals(("".rope + "hi".rope).simplify, "hi".rope)
    assertEquals(("hi".rope.slice(1, 1)).simplify, "".rope)

  }
}
