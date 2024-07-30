
package src.main.scala.archive



final class archive$u002Eworksheet$_ {
def args = archive$u002Eworksheet_sc.args$
def scriptPath = """src/main/scala/archive/archive.worksheet.sc"""
/*<script>*/
  def get(path: Path[K]): Option[Either[List[K], V]] = 
    if !contains(path) then None
    else {
      var currnode = this //root node
      for (key <- path.segs) {
        currnode match {
          case Node(_, children) => currnode = children.find(_.key==key)
          case _ => None
        }
      }
      currnode match {
        case Node(key, child) => 
          val ls = child.map(_.key)
          Some(Left(ls))
        case Leaf(_, payload) => Some(Right(payload))
      }
    }

def contains(path: Path[K]): Boolean = {
    def containsHelper(currTree: Tree[K, V], currPath: List[K]): Boolean = (currTree, currPath) match {
      case (_, Nil) => true // Empty path, so the tree itself is considered to contain it
      case (Leaf(key, _), seg :: Nil) => key == seg // Check if the leaf node contains the segment
      case (Node(key, children), seg :: remaining) =>
        if (key == seg) {
          containsHelper(Node(key, children), remaining)
        } else {
          false
        }
      case _ => false // Invalid path or mismatched tree type
    }
    containsHelper(this, path.segs)
  }

  def get(path: Path[K]): Option[Either[List[K], V]] = 
    if !contains(path) then None
    else {
      var currnode = this //root node
      def find_lnode(curr:Tree[K, V], path:List[K]):Tree[K, V]=
        curr match {
          case Node(key, children) => 
            if !path.isEmpty then 
              find_lnode(children.find(path.head==_.key).getOrElse(Tree.apply(path.head)), path.tail)
            else currnode=Node(key, children)
            currnode
          case Leaf(key, payload) => 
            currnode=Leaf(key, payload)
            currnode
        }
      find_lnode(currnode, path.segs)
      currnode match {
        case Node(key, child) => 
          val ls = child.map(_.key)
          Some(Left(ls))
        case Leaf(_, payload) => Some(Right(payload))
      }
    }
/*</script>*/ /*<generated>*/
/*</generated>*/
}

object archive$u002Eworksheet_sc {
  private var args$opt0 = Option.empty[Array[String]]
  def args$set(args: Array[String]): Unit = {
    args$opt0 = Some(args)
  }
  def args$opt: Option[Array[String]] = args$opt0
  def args$: Array[String] = args$opt.getOrElse {
    sys.error("No arguments passed to this script")
  }

  lazy val script = new archive$u002Eworksheet$_

  def main(args: Array[String]): Unit = {
    args$set(args)
    script.hashCode() // hashCode to clear scalac warning about pure expression in statement position
  }
}

export archive$u002Eworksheet_sc.script as archive$u002Eworksheet

