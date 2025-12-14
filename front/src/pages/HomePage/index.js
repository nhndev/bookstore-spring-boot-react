import React from 'react';
import { Container, Row } from 'react-bootstrap';
import styles from './HomePage.module.scss'

function HomePage() {
  // const [books, setBooks] = useState([])

  // useEffect(() => {
  //   const fetchData = async () => {
  //     try {
  //       const { data } = await bookApi.getAll({page: 1, limit: 6})
  //       setBooks(data)
  //     } catch (error) {
  //       console.log(error)
  //     }
  //   }

  //   fetchData()
  // }, [])
  return (
    <div className="main">
      <Container>
        <div className={styles.booksList}>
          <div className={styles.title}>
            <h2 className={styles.titleHeading}>Sản phẩm mới nhất</h2>
          </div>
          <Row className={styles.row}>
            {/* {books && books.length > 0 ? (
               books.map(book =>
                <Col xl={2} xs={6} key={book._id}>
                  <BookItem data={book} />
                </Col>)
            ) : <Loading />} */}
          </Row>
        </div>
      </Container>
    </div>
  );
}

export default HomePage;
