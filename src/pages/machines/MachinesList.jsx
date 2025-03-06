import { useState, useEffect } from 'react';
import { MACHINE_DATA } from '../../api/mock_data';
import MachineTabelBig from '../../components/machines/MachineTabelBig';

const MachinesList = () => {
  const [machines, setMachines] = useState(MACHINE_DATA);
  const [sortOrderId, setSortOrderId] = useState('desc');
  const [sortOrderSiteId, setSortOrderSiteId] = useState('asc');
  const [text, setText] = useState('');

  const sortMachinesByProp = (prop) => {
    let sortOrder, setSortOrder;
    if (prop === 'id') {
      sortOrder = sortOrderId;
      setSortOrder = setSortOrderId;
    } else if (prop === 'site_id') {
      sortOrder = sortOrderSiteId;
      setSortOrder = setSortOrderSiteId;
    }

    const sortedMachines = [...machines].sort((a, b) => {
      if (sortOrder === 'asc') {
        return a[prop] - b[prop];
      } else {
        return b[prop] - a[prop];
      }
    });
    setMachines(sortedMachines);
    setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
  };

  useEffect(() => {
    const filteredMachines = MACHINE_DATA.filter((m) => {
      return m.id.toString().includes(text.toLowerCase());
    });
    setMachines(filteredMachines);
  }, [text]);

  return (
    <div className='machine-tabel-big-container'>
      <div className='input-group'>
        <input
          type='search'
          id='search'
          className='form-control rounded'
          placeholder='Search'
          value={text}
          onChange={(e) => setText(e.target.value)}
        />
      </div>
      <MachineTabelBig machines={machines} sortMachines={sortMachinesByProp} />
    </div>
  );
};

export default MachinesList;