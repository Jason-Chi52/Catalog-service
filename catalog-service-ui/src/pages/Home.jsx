import { Container, Jumbotron } from 'react-bootstrap'

export default function Home() {
  return (
    <Container className="text-center mt-5">
      <div className="p-5 mb-4 bg-light rounded-3">
        <h1 className="display-4">Welcome to Catalog Service</h1>
        <p className="lead text-muted">
          A simple, full-stack product catalog you can demo anywhere.
        </p>
      </div>
    </Container>
  )
}
