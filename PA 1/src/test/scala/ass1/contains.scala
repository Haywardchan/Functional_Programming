// package ass1
// import munit.FunSuite
// import scala.concurrent.duration.*

// class TreeSuite extends FunSuite {
//   import Tree.Path
//   import Tree.Node
//   import Tree.Leaf

//   extension (s: String) def toPath: Path[String] =
//     if s.isEmpty then Path("" :: Nil)
//     else Path(s.split('/').toList)

//   trait TestTree {
//     val tree = Node("A", List(
//     Leaf("B", "ValueB"),
//     Node("C", List(
//       Leaf("D", "ValueD"),
//       Leaf("E", "ValueE")
//     )),
//     Node("F", List(
//       Leaf("G", "ValueG"),
//       Node("H", List(
//         Leaf("I", "ValueI"),
//         Leaf("J", "ValueJ")
//       ))
//     ))
//   ))
// }

//   test("Tree.contains") {
//     new TestTree {
//       val path1 = Path(List("A", "F", "H", "J"))
//       assertEquals(tree.contains(path1), true)

//       val path2 = Path(List("A", "C", "F", "G"))
//       assertEquals(tree.contains(path2), false)
//     }
//   }

//   override val munitTimeout: Duration = 10.seconds
// }