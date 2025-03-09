import { useParams } from 'react-router-dom';
import { MACHINE_DATA } from '../../api/mock_data';
import { IoTrashOutline, IoPencilOutline } from 'react-icons/io5';
import { Link } from 'react-router-dom';

const MachineDetailBig = () => {
  const { id } = useParams();
  const idAsNumber = Number(id);

  const machine = MACHINE_DATA.find((m) => m.id === idAsNumber);

  if (!machine) {
    return (
      <div>
        <h1>Machine not found</h1>
        <p>There is no machine with id {id}.</p>
      </div>
    );
  }

  return (
    <div>
      <h1>Machine id: {machine.id}</h1>
      <p>Machine status: {machine.state}</p>
      <p>Machine productie status: {machine.prod_state}</p>
      <Link to={`/machines/edit/${machine.id}`} className='btn btn-light'>
        <IoPencilOutline />
      </Link>
    </div>
  );
};

export default MachineDetailBig;
