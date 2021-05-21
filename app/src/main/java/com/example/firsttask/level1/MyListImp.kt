import java.util.*

const val DEFAULT_LIST_SIZE = 10

class MyListImp<E> : List<E>{
    override var size: Int = 0
    var elementData:Array<Any?> = Array(10) { null }

    override fun contains(element: E): Boolean {
        return indexOf(element) != -1
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        var flag = true
        elements.forEach{
            if (indexOf(it)==-1){
                flag = false
            }
            return@forEach
        }
        return flag
    }

    override fun get(index: Int): E {
        return elementData[index] as E
    }

    override fun indexOf(element: E): Int {
        for (i in 0 .. elementData.size){
            if (elementData[i] == element ){
                return i
            }
        }
        return -1
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override fun iterator(): Iterator<E> {
        return MyIterator()
    }

    override fun lastIndexOf(element: E): Int {
        var index:Int = -1
        for (i in 0 until size){
            if (elementData[i] == element){
                index = i
            }
        }
        return index
    }

    fun grow(){
        elementData = elementData.copyOf(size+1)
    }
    fun growTo(size:Int){
        elementData = elementData.copyOf(size)
    }
        //增加
    fun add(element:E){
        if (size >= DEFAULT_LIST_SIZE){
            grow()
        }
        elementData[size++] = element
    }

    //插入
    fun add(element: E,index: Int){
        if (size >= DEFAULT_LIST_SIZE){
            grow()
        }
        for (i in size downTo  index){
            elementData[i] = elementData[i-1]
        }
        elementData[index] = element
        size++
    }

    fun addAll(elements: Collection<E>){
        if (size+ elements.size > DEFAULT_LIST_SIZE){
            growTo(size + elements.size)

        }
        for (i in elements){
            add(i)
        }
    }

    fun set(element:E,index: Int){
        if (index <= size){
            elementData[index]= element
        }
    }

    override fun listIterator(): ListIterator<E> {
        TODO("Not yet implemented")
    }

    override fun listIterator(index: Int): ListIterator<E> {
        TODO("Not yet implemented")
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<E> {
        TODO("Not yet implemented")
    }


    //内部的迭代器对象
    inner class MyIterator<E> : Iterator<E>{
        //下一个iterator的位置
        var cursor = 0
        //最大
        private val limit = this@MyListImp.size


        override fun hasNext(): Boolean {
                return cursor < limit
        }

        override fun next(): E {
            return this@MyListImp.elementData[cursor++] as E
        }

    }

}