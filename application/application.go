package application

import "github.com/devmop/event-stream/events"
import "github.com/devmop/event-stream/users"

type Subsystem func(events.Stream)

var Users = users.Initialise

func Initialise(systems ...Subsystem) events.Stream {
	stream := events.AsyncStream(10)

	for _, system := range systems {
		system(stream)
	}

	return stream
}

func TestInitialise(systems ...Subsystem) events.Stream {
	stream := events.SyncStream()

	for _, system := range systems {
		system(stream)
	}

	return stream
}
