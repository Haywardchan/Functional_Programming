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