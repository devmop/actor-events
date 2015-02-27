package users

import "github.com/devmop/event-stream/events"

func Initialise(stream events.Stream) {
	stream.Register(&users{stream: stream})
}

type users struct {
	stream events.Stream
}

type RegistrationRequest struct {
	email, password string
}

type Registration struct {
	id, email, password string
}

func (l *users) Receive(m events.Message) {
	switch m.Content.(type) {
	case RegistrationRequest:
		r := l.registerUser(m.Content.(RegistrationRequest))
		l.stream.Send(m.ChildMessage(r))
	default:
	}
}

func (l *users) registerUser(r RegistrationRequest) Registration {
	return Registration{id: "1", email: r.email, password: r.password}
}
