package test

import "container/list"
import "testing"
import "github.com/devmop/event-stream/application"
import "github.com/devmop/event-stream/events"
import "github.com/devmop/event-stream/users"

func TestReceivesRegistration(t *testing.T) {
	e, r := setup()

	defer e.Terminate()

	envelope, _ := register(e, r)

	if envelope == nil {
		t.Error("Should have received registration with same id")
		return
	}

	result := envelope.Content.(*users.Registration)

	if result.Email != "email@example.com" {
		t.Errorf("Email should be email@example.com but was %s", result.Email)
	}

	if result.Password != "password" {
		t.Errorf("Password should be password but was %s", result.Password)
	}
}

func TestAlreadyRegistered(t *testing.T) {
	e, r := setup()
	defer e.Terminate()

	register(e, r)
	_, envelope := register(e, r)

	if envelope == nil {
		t.Error("Should have received rejection with same id")
		return
	}

	result := envelope.Content.(*users.AlreadyRegistered)

	if result.Email != "email@example.com" {
		t.Errorf("Email should be email@example.com but was %s", result.Email)
	}
}

func register(e events.Stream, r recorder) (*events.Message, *events.Message) {
	m := events.NewMessage(&users.RegistrationRequest{Email: "email@example.com", Password: "password"})

	e.Send(m)

	reg := findRegistration(m.Id(), r.registrations)
	ex := findExistingResponse(m.Id(), r.existing)

	return reg, ex
}

func findRegistration(id int64, messages *list.List) *events.Message {
	for it := messages.Front(); it != nil; it = it.Next() {
		m := it.Value.(events.Message)
		if m.Id() == id {
			return &m
		}
	}
	return nil
}

func findExistingResponse(id int64, messages *list.List) *events.Message {
	for it := messages.Front(); it != nil; it = it.Next() {
		m := it.Value.(events.Message)
		if m.Id() == id {
			return &m
		}
	}
	return nil
}

func setup() (events.Stream, recorder) {
	e := application.TestInitialise(application.Users)

	r := recorder{list.New(), list.New()}
	e.Register(r)

	return e, r
}

type recorder struct {
	registrations *list.List
	existing      *list.List
}

func (e recorder) Receive(m events.Message) {
	if _, ok := m.Content.(*users.Registration); ok {
		e.registrations.PushBack(m)
	}
	if _, ok := m.Content.(*users.AlreadyRegistered); ok {
		e.existing.PushBack(m)
	}
}

func (e recorder) Terminate() {
}
