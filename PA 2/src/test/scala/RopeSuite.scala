package ass2

// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
class RopeSuite extends munit.FunSuite {
  extension (s: String)
    def rope: Rope = Rope(s)

  def plsfail(f: () => Any): Boolean = {
    try {
      f()
    }
    catch {
      case _ => return true;
    }
    false
  }
  test("apply - basic cases") {
    assertEquals("hello".rope(0), 'h')
    assertEquals("hello".rope(1), 'e')
    assertEquals("hello".rope(2), 'l')
    assertEquals("hello".rope(3), 'l')
    assertEquals("hello".rope(4), 'o')
    assert(plsfail(()=>{"hello".rope(-1)}))
    assert(plsfail(()=>{"hello".rope(5)}))
  }

  test("length - basic cases") {
    assertEquals("hello".rope.length, 5)
    assertEquals(("foo".rope + "bar".rope).length, 6)
    assertEquals(("foo".rope * 3).length, 9)
    assertEquals(("foo".rope * 3).slice(1, 4).length, 3)
    assertEquals(("foo".rope * 20).slice(1, 4).length, 3)
    //empty
    assertEquals("".rope.length, 0)
    //Leaf
    assertEquals(Rope.Leaf("FML").length, 3)
    assertEquals(Rope.Leaf("FM L").length, 4)
    //Slice
    assertEquals(Rope.Slice("FM L".rope, 1, 2).length, 1)
    assertEquals(Rope.Slice("Bye world".rope, 0, 2).length, 2)
    assert(plsfail(()=>Rope.Slice("".rope, 0, 2).length))
    assertEquals(Rope.Slice("".rope, 0, 0).length, 0)
    //Repeat
    assertEquals(Rope.Repeat("".rope, 3).length, 0)
    assertEquals(Rope.Repeat("hello".rope, 11).length, 55)
    //Concat
    assertEquals(Rope.Concat("".rope, "hello".rope).length, 5)
    assertEquals(Rope.Concat("".rope, "".rope).length, 0)

  }

  test("indexOf - basic cases") {
    assertEquals("hello".rope.indexOf("hello", 0), 0)
    assertEquals("hello".rope.indexOf("hello", 1), -1) // NOT FOUND
    assertEquals("hello".rope.indexOf("l", 0), 2)
    assertEquals("hello".rope.indexOf("l", 3), 3)
    assertEquals("hello".rope.indexOf("o", 4), 4)
    assertEquals("a".rope.indexOf("a", -10), 0)//invalid index
  }

  test("toString - basic cases") {
    assertEquals("hello".rope.toString, "hello")
    assertEquals(("foo".rope + "bar".rope).toString, "foobar")
    assertEquals(("foo".rope * 3).toString, "foofoofoo")
    assertEquals(("foo".rope * 3).slice(1, 4).toString, "oof")
    assertEquals(("hello".rope * 2147483647).slice(2, 3).toString, "l")
    assertEquals(("hello".rope * 2147483647).slice(2, 10).toString, "llohello")
    assertEquals(("hello".rope * 2147483647).slice(2, 11).toString, "llohelloh")
    assertEquals(("hello".rope * 2147483647).slice(2, 16).toString, "llohellohelloh")
    assertEquals(("hello".rope * 2147483647).slice(0, 5).toString, "hello")
    assertEquals(("hello".rope * 2147483647).slice(5, 10).toString, "hello")
    assertEquals(("hel".rope * 2147483647).slice(0, 1).toString, "h")
    assertEquals(("".rope * 214748364).slice(0, 0).toString, "")
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
    assertEquals("hello".rope.split("").map(_.toString), List("h","e","l","l","o"))
    assertEquals("aaa".rope.split("a").map(_.toString), List("","","",""))
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
    val hello = "hello".rope
    assertEquals(hello.replace("", "x").toString,
    "xhxexlxlxox")

    assertEquals((Rope.Leaf("a") + Rope.Leaf("b")).replace("ab", "c").toString, "c")
  }

  test("duplicate - basic cases") {
    assertEquals("hello".rope.duplicate(0, 2, 0).toString, "llo")
    assertEquals("hello".rope.duplicate(0, 2, 1).toString, "hello")
    assertEquals("hello".rope.duplicate(0, 2, 2).toString, "hehello")
    assertEquals("hello".rope.duplicate(1, 3, 3).toString, "helelello")
  }

  test("simplify - basic cases") {
    assertEquals("hello".rope.simplify, Rope.Leaf("hello"))
    assertEquals(("foo".rope + "bar".rope).simplify, Rope.Leaf("foo") + Rope.Leaf("bar"))
    assertEquals(("foo".rope * 0).simplify, Rope.empty)
    assertEquals(("foo".rope * 1).simplify, Rope.Leaf("foo"))
    assertEquals("dragonslayer".rope.slice(0, 7).simplify, Rope.Leaf("dragons"))
    assertEquals(("Replace".rope + "ment".rope).slice(5,11).simplify, Rope.Concat(Rope.Leaf("ce"), Rope.Leaf("ment")))
    assertEquals(("Replace".rope + "ment".rope).slice(5,7).simplify, Rope.Leaf("ce"))
    assertEquals(("Replace".rope + "ment".rope).slice(8,10).simplify, Rope.Leaf("en"))
    assertEquals(("Replace".rope * 3).slice(8,10).simplify, Rope.Leaf("ep"))
    assertEquals(("Replace".rope * 3).slice(5,8).simplify, Rope.Leaf("ce") + Rope.Leaf("R"))
    assertEquals(("hello".rope * 2147483647).slice(2, 16).simplify, (("llo".rope+("hello".rope+"hello".rope))+"h".rope))
  }
  test("splitAt - basic cases") {
    assertEquals("hello".rope.splitAt(2)._1.toString, "he")
    assertEquals("hello".rope.splitAt(2)._2.toString, "llo")
    assertEquals(("foo".rope + "bar".rope).splitAt(3)._1.toString, "foo")
    assertEquals(("foo".rope + "bar".rope).splitAt(3)._2.toString, "bar")
    assertEquals(("foo".rope + "bar".rope).splitAt(4)._1.toString, "foob")
    assertEquals(("foo".rope + "bar".rope).splitAt(4)._2.toString, "ar")
    assertEquals(("foo".rope + "bar".rope).splitAt(2)._1.toString, "fo")
    assertEquals(("foo".rope + "bar".rope).splitAt(2)._2.toString, "obar")
    assertEquals(("foo".rope + "bar".rope).splitAt(0)._1.toString, "")
    assertEquals(("foo".rope + "bar".rope).splitAt(0)._2.toString, "foobar")
    assertEquals(("foo".rope + "bar".rope).splitAt(6)._1.toString, "foobar")
    assertEquals(("foo".rope + "bar".rope).splitAt(6)._2.toString, "")
    try{
      assert(plsfail(()=>("foo".rope + "bar".rope).splitAt(-1)._1.toString))
      assert(plsfail(()=>("foo".rope + "bar".rope).splitAt(-1)._2.toString))
      assert(plsfail(()=>("foo".rope + "bar".rope).splitAt(7)._1.toString))
      assert(plsfail(()=>("foo".rope + "bar".rope).splitAt(7)._2.toString))
    }
    assertEquals(("foo".rope * 3).splitAt(0)._2.toString, "foofoofoo")
    assertEquals(("foo".rope * 3).splitAt(3)._2.toString, "foofoo")
    assertEquals(("foo".rope * 3).splitAt(7)._2.toString, "oo")
    assertEquals(("".rope * 3).splitAt(0)._2.toString, "")

    assertEquals(("hello".rope * 2147483647).slice(2, 16).splitAt(3)._2.toString, "hellohelloh")
    // assertEquals(("foo".rope * 0).simplify, Rope.empty)
    
  }
}
