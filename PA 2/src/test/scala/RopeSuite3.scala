package ass2

// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
class RopeSuite3 extends munit.FunSuite {
  extension (s: String)
    def rope: Rope = Rope(s)
    
  val concatTest = "Hello, ".rope + "world!".rope
  val sliceTest = "circumstances".rope.slice(3, 6)
  val repeatTest = "YMCA".rope * 3
  test("apply - basic cases") {
    
    assertEquals("hello".rope(0), 'h')
    assertEquals("hello".rope(1), 'e')
    assertEquals("hello".rope(2), 'l')
    assertEquals("hello".rope(3), 'l')
    assertEquals("hello".rope(4), 'o')
    assertEquals(concatTest(4), 'o')
    assertEquals(sliceTest(2), 'm')
    assertEquals(repeatTest(2), 'C')
  }

  test("apply - exceptions"){
    intercept[IndexOutOfBoundsException]{
      "hello, world".rope(12)
    }
    intercept[IndexOutOfBoundsException]{
      "hello".rope(-1)
    }
  }

  test("length - basic cases") {
    assertEquals("hello".rope.length, 5)
    assertEquals(("foo".rope + "bar".rope).length, 6)
    assertEquals(("foo".rope * 3).length, 9)
    assertEquals(("foo".rope * 3).slice(1, 4).length, 3)
  }

  test("indexOf - basic cases") {
    assertEquals("hello".rope.indexOf("hello", 0), 0)
    assertEquals("hello".rope.indexOf("hello", 1), -1) // NOT FOUND
    assertEquals("hello".rope.indexOf("l", 0), 2)
    assertEquals("hello".rope.indexOf("l", 3), 3)
    assertEquals("hello".rope.indexOf("o", 4), 4)
  }

  test("toString - basic cases") {
    assertEquals("hello".rope.toString, "hello")
    assertEquals(("foo".rope + "bar".rope).toString, "foobar")
    assertEquals(("foo".rope * 3).toString, "foofoofoo")
    assertEquals(("foo".rope * 3).slice(1, 4).toString, "oof")
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
    assertEquals("computerscience".rope.split("e").map(_.toString), List("comput", "rsci", "nc", ""))
    assertEquals("hello".rope.split("h").map(_.toString), List("", "ello"))
    assertEquals("hello".rope.split("e").map(_.toString), List("h", "llo"))
    assertEquals("hello".rope.split("l").map(_.toString), List("he", "", "o"))
    assertEquals("hello".rope.split("o").map(_.toString), List("hell", ""))
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
    assertEquals( "I am a boy".rope.replace("a", "two").toString, "I twom two boy")
    assertEquals( "I am a boy".rope.replace("", "two").toString, "twoItwo twoatwomtwo twoatwo twobtwootwoytwo")
  }

  test("duplicate - basic cases") {
    assertEquals("hello".rope.duplicate(0, 2, 0).toString, "llo")
    assertEquals("hello".rope.duplicate(0, 2, 1).toString, "hello")
    assertEquals("hello".rope.duplicate(0, 2, 2).toString, "hehello")
    assertEquals("hello".rope.duplicate(1, 2, 2).toString, "heello")
  }

  test("simplify - basic cases") {
    assertEquals("hello".rope.simplify, Rope.Leaf("hello"))
    assertEquals(("foo".rope + "bar".rope).simplify, Rope.Leaf("foo") + Rope.Leaf("bar"))
    assertEquals(("foo".rope + "".rope).simplify, Rope.Leaf("foo"))
    assertEquals(("foo".rope * 0).simplify, Rope.empty)
    assertEquals(("foo".rope * 1).simplify, Rope.Leaf("foo"))
    assertEquals(("foo".rope * 2).simplify, Rope.Leaf("foo")+Rope.Leaf("foo"))
    assertEquals(("foofighters".rope.slice(0, 3) * 2).simplify, Rope.Leaf("foo")+Rope.Leaf("foo"))
    assertEquals("dragonslayer".rope.slice(0, 7).simplify, Rope.Leaf("dragons"))
  }

  test("apply - basic cases") {
    val rope = "hello".rope
    assertEquals(rope(0), 'h')
    assertEquals(rope(2), 'l')
    assertEquals(rope(4), 'o')
  }

  test("apply - index out of bounds") {
    val rope = "hello".rope
    intercept[IndexOutOfBoundsException] {
      rope(-1)
    }
    intercept[IndexOutOfBoundsException] {
      rope(5)
    }
  }

  test("length - basic cases") {
    assertEquals("hello".rope.length, 5)
    assertEquals(("foo".rope + "bar".rope).length, 6)
    assertEquals(("foo".rope * 3).length, 9)
  }

  test("toString - basic cases") {
    assertEquals("hello".rope.toString, "hello")
    assertEquals(("foo".rope + "bar".rope).toString, "foobar")
    assertEquals(("foo".rope * 3).toString, "foofoofoo")
    assertEquals(("foo".rope * 3).slice(1, 4).toString, "oof")
  }

  test("indexOf - basic cases") {
    val rope = "hello world".rope
    assertEquals(rope.indexOf("hello", 0), 0)
    assertEquals(rope.indexOf("world", 0), 6)
    assertEquals(rope.indexOf("o", 0), 4)
    assertEquals(rope.indexOf("o", 5), 7)
    assertEquals(rope.indexOf("foo", 0), -1)
  }
  test("insert - basic cases") {
    val rope = "hello".rope
    val newRope = rope.insert(" world".rope, 5)
    assertEquals(newRope.toString, "hello world")
    assertEquals(newRope.length, 11)
  }

  test("insert - index out of bounds") {
    val rope = "hello".rope
    intercept[IndexOutOfBoundsException] {
      rope.insert(" world".rope, -1)
    }
    intercept[IndexOutOfBoundsException] {
      rope.insert(" world".rope, 6)
    }
  }

  test("delete - basic cases") {
    val rope = "hello world".rope
    val newRope = rope.delete(5, 11)
    println(newRope.toString())
    assertEquals(newRope.toString(), "hello")
    assertEquals(newRope.length, 5)
  }

  test("delete - index out of bounds") {
    val rope = "hello world".rope
    intercept[IndexOutOfBoundsException] {
      rope.delete(-1, 5)
    }
    intercept[IndexOutOfBoundsException] {
      rope.delete(0, 12)
    }
    intercept[IndexOutOfBoundsException] {
      rope.delete(7, 6)
    }
  }

  test("split - basic cases") {
    val rope = "hello world".rope
    val splitted = rope.split(" ")
    assertEquals(splitted.map(_.toString), List("hello", "world"))
  }

  test("replace - basic cases") {
    val rope = "hello world".rope
    val newRope = rope.replace("world", "universe")
    assertEquals(newRope.toString, "hello universe")
    assertEquals(newRope.length, 14)
  }

  test("replace - empty text") {
    val rope = "hello world".rope
    val newRope = rope.replace("", "!")
    assertEquals(newRope.toString, "!h!e!l!l!o! !w!o!r!l!d!")
    assertEquals(newRope.length, 23)
  }

  test("duplicate - basic cases") {
    val rope = "hello world".rope
    val newRope = rope.duplicate(0, 5, 3)
    assertEquals(newRope.toString, "hellohellohello world")
    assertEquals(newRope.length, 21)
  }

  test("duplicate - remove substring") {
    val rope = "hello world".rope
    val newRope = rope.duplicate(6, 11, 0)
    assertEquals(newRope.toString, "hello ")
    assertEquals(newRope.length, 6)
  }

  test("simplify - empty Rope") {
    assertEquals("".rope.simplify, Rope.empty)
  }

  test("simplify - repeated empty Rope") {
    assertEquals(("".rope * 3).simplify, Rope.empty)
  }

  test("simplify - repeated single character") {
    assertEquals(("a".rope * 4).simplify, (("a".rope + "a".rope) + "a".rope) + "a".rope)
  }

  test("simplify - repeated substring") {
    assertEquals(("abc".rope * 2).simplify, "abc".rope + "abc".rope)
  }

  test("simplify - repeated substring with concatenation") {
    assertEquals(("abc".rope * 2 + "def".rope).simplify, ("abc".rope + "abc".rope) + "def".rope)
  }

  test("simplify - sliced Rope") {
    assertEquals(("abcdef".rope.slice(0, 3)).simplify, Rope.Leaf("abc"))
    assertEquals(("abcdef".rope.slice(2, 5)).simplify, Rope.Leaf("cde"))
    assertEquals(("abcdef".rope.slice(1, 4)).simplify, Rope.Leaf("bcd"))
  }

  test("simplify - complex case") {
    val rope = ((("hello".rope * 3) + "world".rope) + ("foo".rope * 0)) + ("bar".rope * 1)
    assertEquals(rope.simplify, (((("hello".rope + "hello".rope) + "hello".rope) + "world".rope) + "bar".rope))
  }

}
