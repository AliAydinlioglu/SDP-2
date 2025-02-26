export default function Loader() {
  return (
    <div className='d-flex flex-column align-items-center' data-cy="loader">
      <div className='spinner-border'>
        <span className='visually-hidden'>Loading...</span>
      </div>
    </div>
  );
}