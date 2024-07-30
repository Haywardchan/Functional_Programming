package ass1

import javax.net.ssl.TrustManager

enum Tree[K, V] extends TreeInterface[K, V]:
  private case Leaf(key: K, payload: V)
  private case Node(key: K, children: List[Tree[K, V]])

  import Tree.Path

  def contains(path: Path[K]): Boolean =
    if (path.segs.isEmpty) then false
    else this match
      case Node(key, children) => // current dir is folder
        if (key != path.segs.head) then false // folder name mismatch
        else if (path.segs.tail.isEmpty) then true // current folder match, no next, this is dest
        else childrencontains(children, Path(path.segs.tail)) // not dest, find child for next folder name

      case Leaf(key, payload) => // current dir is file
        if (!path.segs.tail.isEmpty) then false // this is not dest
        else if (key != path.segs.head) then false // wrong file
        else true // correct file, return true

  def childrencontains(children: List[Tree[K, V]], path: Path[K]): Boolean =
    if (children.isEmpty) then false // folder have no correct subdir
    else if (children.head.key == path.segs.head) then children.head.contains(path)
    else childrencontains(children.tail, path)

  def get(path: Path[K]): Option[Either[List[K], V]] = 
    if (path.segs.isEmpty) then None  // invalid path
    else this match
      case Node(key, children) => // current dir is folder
        if (key != path.segs.head) then None // folder name mismatch
        else if (path.segs.tail.isEmpty) then Some(Left(children.map(_.key))) // this is the destination, ls
        else childrenget(children, Path(path.segs.tail)) // not dest, find children
      
      case Leaf(key, payload) => // current dir is file
        if (!path.segs.tail.isEmpty) then None // this is not the dest
        else if (key != path.segs.head) then None // wrong file
        else Some(Right(payload)) // correct file, return payload
      
  def childrenget(children: List[Tree[K, V]], path: Path[K]): Option[Either[List[K], V]] = // iterator
    if (children.isEmpty) then None // folder have no correct subdir
    else if (children.head.key == path.segs.head) then children.head.get(path) // found children, continue get
    else childrenget(children.tail, path) // not found, continue

  def flatten: List[(Path[K], V)] = this match
    case Leaf(key, payload) => List((Path(List(key)), payload)) // leaf: only return this file
    case Node(key, children) => flattenwithpath(Path(List())) // node: pass current path to children, recursively list

  def flattenwithpath(path: Path[K]): List[(Path[K], V)] = this match
    case Leaf(key, payload) => List((Path(path.segs :+ key), payload))
    case Node(key, children) => children.map(_.flattenwithpath(Path(path.segs:+key))).flatten

  def updated(path: Path[K]): Tree[K, V] = // last step is a node
    if (path.segs.isEmpty) then throw Tree.IllegalPathException // Path is empty
    else if (this.key != path.segs.head) then throw Tree.IllegalPathException // root is not head
    else this match
      case Leaf(key, payload) => // current is Leaf, replace by node
        if (path.segs.tail.isEmpty) then Node(key, Nil) // dest, only replace leaf with folder
        else if (path.segs.tail.tail.isEmpty) then Node(key, List(Node(path.segs.tail.head, Nil))) // next is dest, prevent throw by doing next step
        else Node(key, List(Node(path.segs.tail.head, Nil).updated(Path(path.segs.tail)))) // recurse


      case Node(key, children) => // current is node
        if (path.segs.tail.isEmpty) then Node(key, Nil) // current node is dest, replace folder with new folder
        else if (children.isEmpty) then  // if no children then just insert and recurse
          if (path.segs.tail.tail.isEmpty) then Node(key, List(Node(path.segs.tail.head, Nil))) // next is dest
          else Node(key, List(Node(path.segs.tail.head, Nil).updated(Path(path.segs.tail)))) // recurse
        

        // else, current.children is NOT empty, and Path is at least 2 layers, begin node copying 
        else
          if (!childrencontains(children, Path(List(path.segs.tail.head)))) then
            Node(key, children:+ (Node(path.segs.tail.head, Nil).updated(Path(path.segs.tail))))
          else
          Node(key, children.map(child => child match
          
          case Leaf(key, payload) =>
            if (key == path.segs.tail.head) then child.updated(Path(path.segs.tail))
            else child

          case Node(key, children) =>
            if (key == path.segs.tail.head) then child.updated(Path(path.segs.tail))
            else child

          )) // wrong key then copy, right key then replace and recurse 
            

  def updated(path: Path[K], payload: V): Tree[K, V] = // last step is a leaf
    if (path.segs.isEmpty) then throw Tree.IllegalPathException // Path is empty
    else if (this.key != path.segs.head) then throw Tree.IllegalPathException // root is not head
    else 
      this match
      case Leaf(key, cpayload) => // current is Leaf, replace by node
        if (path.segs.tail.isEmpty) then 
          Leaf(key, payload) // dest, only replace leaf with folder
        else if (path.segs.tail.tail.isEmpty) then
          Node(key, List(Leaf(path.segs.tail.head, payload))) // next is dest, prevent throw by doing next step
        else
          Node(key, List(Leaf(path.segs.tail.head, payload).updated(Path(path.segs.tail), payload))) // recurse


      case Node(key, children) => // current is node
        if (path.segs.tail.isEmpty) then 
          Leaf(key, payload) // current node is dest, replace folder with new folder
        else if (children.isEmpty) then  // if no children then just insert and recurse
          if (path.segs.tail.tail.isEmpty) then 
            Node(key, List(Leaf(path.segs.tail.head, payload))) // next is dest

          else
            Node(key, List(Leaf(path.segs.tail.head, payload).updated(Path(path.segs.tail), payload))) // recurse
        
        // else, current.children is NOT empty, and Path is at least 2 layers, begin node copying 
        else 

          if (!childrencontains(children, Path(List(path.segs.tail.head)))) then
            Node(key, children:+ (Leaf(path.segs.tail.head, payload).updated(Path(path.segs.tail), payload)))
          else

          // If children contains
          Node(key, children.map(child => child match
          
          case Leaf(key, cpayload) =>
            if (key == path.segs.tail.head) then
              child.updated(Path(path.segs.tail), payload)
            else 
              child

          case Node(key, children) =>
            if (key == path.segs.tail.head) then
              child.updated(Path(path.segs.tail), payload)
            else
              child

          )) // wrong key then copy, right key then replace and recurse 

object Tree extends TreeComp:
  def apply[K, V](key: K): Tree[K, V] = Node(key, Nil)

  def apply[K, V](path: Path[K]): Tree[K, V] =
    if (path.segs.isEmpty) then throw Tree.IllegalPathException
    else Node(path.segs.head, Nil).updated(path)
    

  def apply[K, V](path: Path[K], payload: V): Tree[K, V] =
    if (path.segs.isEmpty) then throw Tree.IllegalPathException
    else Node(path.segs.head, Nil).updated(path, payload)
