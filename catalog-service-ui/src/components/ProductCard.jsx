import { Card, Button } from 'react-bootstrap'

export default function ProductCard({ product, onEdit, onDelete }) {
  return (
    <Card className="h-100">
      <Card.Body className="d-flex flex-column">
        <Card.Title>{product.name}</Card.Title>
        <Card.Text className="flex-grow-1">
          {product.description}
        </Card.Text>
        <div className="mb-3 fw-bold">${product.price}</div>
        <div className="d-flex justify-content-between">
          <Button variant="warning" size="sm" onClick={() => onEdit(product)}>
            Edit
          </Button>
          <Button variant="danger" size="sm" onClick={() => onDelete(product.id)}>
            Delete
          </Button>
        </div>
      </Card.Body>
    </Card>
  )
}
