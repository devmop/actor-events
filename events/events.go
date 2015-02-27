package events

import "math/rand"

type Receiver interface {
	Receive(any Message)
}

type Message struct {
	id      int64
	Content interface{}
}

type Stream interface {
	Register(Receiver)
	Send(Message)
}

type inproc struct {
	receivers []Receiver
}

func (b *inproc) Register(a Receiver) {
	b.receivers = append(b.receivers, a)
}

func (b *inproc) Send(message Message) {
	for _, a := range b.receivers {
		a.Receive(message)
	}
}

func NewStream() Stream {
	return &inproc{receivers: make([]Receiver, 0, 100)}
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
