package ass2

import scala.collection.IndexedSeqView.Slice

trait RopeImpl:
  // The following code confines the type of `this` to `this`, so you can do
  // what you want as if you are inside the `Rope` class.
  self: Rope =>

  import Rope.{Leaf, Concat, Slice, Repeat}
  // NOTE: DO NOT MODIFY THE CODE ABOVE THIS LINE.

//done
  def splitAt(index: Int): (Rope, Rope) = 
    if index < 0 || index > this.length then throw new IndexOutOfBoundsException
    else this match
    case Leaf(text) => (Leaf(text.substring(0, index)), Leaf(text.substring(index, this.length)))
    case Concat(left, right) => 
      if index >= left.length then (left + right.splitAt(index-left.length)._1, right.splitAt(index-left.length)._2)
      else (left.splitAt(index)._1, (left.splitAt(index)._2 + right))
    case Repeat(rope, count) => this.simplify.splitAt(index)
    case Slice(rope, start, end) => this.simplify.splitAt(index)

//done
  def apply(index: Int): Char = 
    if index < 0 || index > this.length then throw new IndexOutOfBoundsException
    else this match
      case Leaf(text) => text.charAt(index)
      case Concat(left, right) =>
        if left.length <= index then right.apply(index - left.length)
        else left.apply(index)
      case Repeat(rope, count) => rope.apply(index % rope.length)
      case Slice(rope, start, end) => 
        if start < 0 || start > rope.length || end < 0 || end > rope.length then throw new IndexOutOfBoundsException
        else this.simplify.apply(index)
//done
  lazy val length: Int = this match
    case Leaf(text) => text.length()
    case Concat(left, right) => left.length + right.length
    case Repeat(rope, count) => rope.length * count
    case Slice(rope, start, end) => 
      if start < 0 || start > rope.length || end < 0 || end > rope.length then throw new IndexOutOfBoundsException
      else end - start

//done
  override def toString(): String = 
    this match
    case Leaf(text) => text
    case Concat(left, right) => left.toString() + right.toString()
    case Repeat(rope, count) => rope.toString() * count
    case Slice(rope, start, end) => 
      if start == end then ""
      else rope match
      //optimize the case
      case Repeat(repeat, count) => 
        if end / repeat.length > count then throw new IndexOutOfBoundsException
        else Repeat(repeat, (end - start) / repeat.length + 2).toString().
        slice(start % repeat.length, start % repeat.length + end - start)
      case Leaf(text) => text.slice(start, end)
      case Concat(left, right) => 
        if(start >= left.length && end >= left.length) then right.slice(start - left.length, end - left.length).toString()
        else if(start < left.length && end < left.length) then left.slice(start, end).toString()
        else left.slice(start, left.length).toString() + right.slice(0, end - left.length).toString()
      case Slice(rope2, start2, end2) => rope2.slice(start2, end2).toString()

//half done
  def indexOf(text: String, start: Int): Int = 
    this match
      case Leaf(st) => 
        if start < 0 then 0 
        else 
          val index = st.substring(start, this.length).indexOf(text) 
          if index >= 0 then index + start
          else -1
      case Concat(left, right) => //have to refine this case
        if (left + right).toString().substring(start, this.length).indexOf(text) == -1 then -1
        else (left + right).toString().substring(start, this.length).indexOf(text) + start

      case Repeat(rope, count) => 
        //Out of bound
        val offset = (rope.toString() * (text.length / rope.length + 2)).indexOf(text, start % rope.length)
        if offset == -1 then -1
        else if text.length + start + offset - start % rope.length >= this.length then -1
        else start + offset - start % rope.length //search entire string including cross part

      case Slice(rope, start1, end1) => 
        rope match
        case Leaf(st) => 
          if st.substring(start1 + start, end1).indexOf(text) == -1 then -1
          else st.substring(start1 + start, end1).indexOf(text) + start // find from start
        case _ => 
          if Slice(rope, start1, end1).simplify.indexOf(text, start) == -1 then -1
          else Slice(rope, start1, end1).simplify.indexOf(text, start) + start1

//done
  def insert(rope: Rope, at: Int): Rope = 
    if at < 0 || at > this.length then throw new IndexOutOfBoundsException
    else if at == 0 then Concat(rope, this)
    else if at == this.length then Concat(this, rope)
    else Concat(Concat(Slice(this, 0, at), rope), Slice(this, at, this.length))

//done
  def delete(start: Int, end: Int): Rope = 
    if start < 0 || start > this.length then throw new IndexOutOfBoundsException
    if end < 0 || end > this.length then throw new IndexOutOfBoundsException
    if start > end then throw new IndexOutOfBoundsException
    if start == 0 then Slice(this, end, this.length)
    else if start == end then this
    else Concat(Slice(this, 0, start), Slice(this, end, this.length))

// done
  def split(separator: String): List[Rope] = 
    if separator.isEmpty() then this.toString().split(separator).map(Leaf(_)).toList
    else this match
      case Leaf(text) => 
        if this.indexOf(separator, 0) == -1 then List(Leaf(text))
        else 
          val llen = Leaf(text).splitAt(this.indexOf(separator, 0))._1.length
          List(Leaf(text).splitAt(this.indexOf(separator, 0))._1) ::: Leaf(text).splitAt(this.indexOf(separator, 0))._2.
          delete(this.indexOf(separator, 0) - llen, this.indexOf(separator, 0) + separator.length() - llen).split(separator)
      case Concat(left, right) => Leaf((left + right).toString).split(separator)
      case Repeat(rope, count) => this.simplify.split(separator)
      case Slice(rope, start, end) => this.simplify.split(separator)
    
//done
  def replace(text: String, replacement: String): Rope = 
    Leaf(this.toString().replaceAll(text, replacement))

//done
  def duplicate(start: Int, end: Int, times: Int): Rope = 
    if start < 0 || start > this.length then throw new IndexOutOfBoundsException
    if end < 0 || end > this.length then throw new IndexOutOfBoundsException
    if times < 0 || start > end then throw new IndexOutOfBoundsException
    if times == 0 then delete(start, end)
    else if start == 0 then Concat(Repeat(Slice(this, start, end), times), slice(end, this.length))
    else if end == this.length then Concat(slice(0, start), Repeat(Slice(this, start, end), times))
    else Concat(slice(0, start) ,Concat(Repeat(Slice(this, start, end), times), slice(end, this.length)))

// mostly finished
  def simplify: Rope = 
    if this.length == 0 then Leaf("") else this match
      case Slice(rope, start, end) => 
        rope match
        case Leaf(text) =>
          Leaf(text.substring(start, end))
        case Concat(left, right) => 
          if(start >= left.length && end >= left.length) then right.slice(start - left.length, end - left.length).simplify
          else if(start < left.length && end < left.length) then left.slice(start, end).simplify
          else Concat(left.slice(start, left.length).simplify, right.slice(0, end - left.length).simplify).simplify
        case Slice(rope2, start2, end2) => 
          rope2.slice(start + start2, end + start2).simplify
        case Repeat(rope, count) => 
          if start % rope.length <= end % rope.length then 
            rope.slice(start % rope.length, end % rope.length).simplify
          else if end % rope.length == 0 then 
            (rope.slice(start % rope.length, rope.length).simplify + 
            Repeat(rope, (end-start)/rope.length)).simplify
          else if start % rope.length==0 then
            (Repeat(rope, (end-start)/rope.length).simplify + 
              rope.slice(0, end % rope.length).simplify).simplify
          else 
            (rope.slice(start % rope.length, rope.length).simplify + 
            Repeat(rope, (end-start)/rope.length).simplify +
            rope.slice(0, end % rope.length).simplify).simplify
      case Repeat(rope, count) => 
        if count == 0 then Leaf("")
        else if rope.length == 0 then Leaf("")
        else Concat(Repeat(rope.simplify, count - 1).simplify, rope).simplify
      case Leaf(text) => Leaf(text)
      case Concat(left, right) => 
        if left.length!=0 && right.length!=0 then Concat(left.simplify, right.simplify)
        else if left.length==0 then right.simplify
        else left.simplify
    
    
