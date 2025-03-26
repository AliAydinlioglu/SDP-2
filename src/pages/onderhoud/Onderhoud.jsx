import { useParams } from 'react-router';
import { ONDERHOUD_DATA } from '../../api/mock_data';

export default function Onderhoud() {
  const {id} = useParams();
  const onderhoud = ONDERHOUD_DATA.find((o) => o.id === Number(id));
  return (
    <div>
      <h1>Onderhoud</h1>
      <p>Details about onderhoud will be displayed here.</p>
    </div>
  );
};