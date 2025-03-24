import { useLocation } from 'react-router-dom'; 

const NotFound = () => {
  const { pathname } = useLocation(); 

  return (
    <div>
      <h1>Page not found</h1>
      <p>There is no page with {pathname}, try something different.</p> 
    </div>
  );
};
export default NotFound;
