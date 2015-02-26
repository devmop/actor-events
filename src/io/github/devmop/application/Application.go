package application

import "io/github/devmop/actors"

type Subsystem interface {
	initialise(actors.Bus)
}
