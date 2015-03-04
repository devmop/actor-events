package users

import "github.com/devmop/event-stream/events"

func Initialise(stream events.Stream) {
	stream.Register(&users{stream: stream, registered: make(map[string]*Registration)})
}

type users struct {
	stream     events.Stream
	registered map[string]*Registration
}

type RegistrationRequest struct {
	Email, Password string
}

type Registration struct {
	Id, Email, Password string
}

type AlreadyRegistered struct {
	Email string
}

func (l *users) Receive(m events.Message) {
	switch m.Content.(type) {
	case *RegistrationRequest:
		l.registerUser(m.Content.(*RegistrationRequest), m)
	default:
	}
}

func (l *users) Terminate() {
}

func (l *users) registerUser(r *RegistrationRequest, m events.Message) {
	_, exists := l.registered[r.Email]
	if exists {
		l.stream.Send(m.ChildMessage(&AlreadyRegistered{Email: r.Email}))
	} else {
		reg := &Registration{Id: "1", Email: r.Email, Password: r.Password}
		l.registered[reg.Email] = reg
		l.stream.Send(m.ChildMessage(reg))
	}
}
