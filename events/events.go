package events

import "math/rand"

const terminate = -1

type Receiver interface {
	Receive(any Message)
	Terminate()
}

type Message struct {
	id      int64
	Content interface{}
}

type Stream interface {
	Register(Receiver)
	Send(Message)
	Terminate()
}

func DefaultAsyncStream() Stream {
	return AsyncStream(4)
}

func AsyncStream(workers int) Stream {
	stream := &async{stream: stream(), input: make(chan Message, 100)}

	for i := 0; i < workers; i++ {
		go stream.Work()
	}

	return stream
}

func SyncStream() Stream {
	return stream()
}

func stream() *subscribers {
	return &subscribers{receivers: make([]Receiver, 0, 100)}
}

func NewMessage(c interface{}) Message {
	return Message{id: rand.Int63(), Content: c}
}

func (m Message) ChildMessage(c interface{}) Message {
	return Message{id: m.id, Content: c}
}

func (m Message) Id() int64 {
	return m.id
}

type subscribers struct {
	receivers []Receiver
}

func (b *subscribers) Register(a Receiver) {
	b.receivers = append(b.receivers, a)
}

func (b *subscribers) Send(message Message) {
	for _, a := range b.receivers {
		a.Receive(message)
	}
}

func (b *subscribers) Terminate() {
	for _, a := range b.receivers {
		a.Terminate()
	}
}

type async struct {
	stream Stream
	input  chan Message
}

func (b *async) Register(a Receiver) {
	b.stream.Register(a)
}

func (b *async) Send(message Message) {
	b.input <- message
}

func (b *async) Terminate() {
	b.input <- Message{id: terminate, Content: 0}
}

func (b *async) Work() {
	for a := range b.input {
		b.stream.Send(a)
	}
}
