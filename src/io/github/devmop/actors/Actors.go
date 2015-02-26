package actors

type Actor interface {
	Receive(any Message)
}

type Message interface {
	Id() int64
}

type Bus interface {
	Register(Actor)
	Send(Message)
}

type bus struct {
	actors []Actor
}

func (b *bus) Register(a Actor) {
	b.actors = append(b.actors, a)
}

func (b *bus) Send(message Message) {
	for _, a := range b.actors {
		a.Receive(message)
	}
}

func NewBus() Bus {
	return &bus{actors: make([]Actor, 0, 100)}
}
