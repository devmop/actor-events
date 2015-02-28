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

type inprocSync struct {
	receivers []Receiver
}

func (b *inprocSync) Register(a Receiver) {
	b.receivers = append(b.receivers, a)
}

func (b *inprocSync) Send(message Message) {
	for _, a := range b.receivers {
		a.Receive(message)
	}
}

func (b *inprocSync) Terminate() {
	for _, a := range b.receivers {
		a.Terminate()
	}
}

type inproc struct {
	receivers []chan Message
}

func (b *inproc) Register(a Receiver) {
	ch := make(chan Message, 100)
	b.receivers = append(b.receivers, ch)
	go actor(ch, a)
}

func (b *inproc) Send(message Message) {
	for _, a := range b.receivers {
		a <- message
	}
}

func (b *inproc) Terminate() {
	b.Send(Message{id: terminate, Content: 0})
}

func AsyncStream() Stream {
	return &inproc{receivers: make([]chan Message, 0, 100)}
}

func SyncStream() Stream {
	return &inprocSync{receivers: make([]Receiver, 0, 100)}
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

func actor(ch chan Message, r Receiver) {
	for {
		m := <-ch
		if m.id == terminate {
			r.Terminate()
			return
		} else {
			r.Receive(m)
		}
	}
}
