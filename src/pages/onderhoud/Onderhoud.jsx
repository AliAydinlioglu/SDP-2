import { useParams } from 'react-router';
import useSWR from 'swr';
import { getById } from '../../api';
import AsyncData from '../../components/AsyncData';

export default function Onderhoud() {
  const {id} = useParams();
  const {
    data: onderhoud,
    loading: onderhoudLoading,
    error: onderhoudError,
  } = useSWR(`onderhoud/${id}`, getById);
  
  if (!onderhoud) {
    return <p>Onderhoud not found</p>;
  }

  return (
    <div>
      <AsyncData loading={onderhoudLoading} error={onderhoudError}>
        <h1>Onderhoud Details</h1>
        <p><strong>Machine:</strong> {onderhoud.machine_id}</p>
        <p><strong>Datum:</strong> {new Date(onderhoud.datum).toLocaleDateString()}</p>
        <p><strong>Start Tijd:</strong> {new Date(onderhoud.startTijd).toLocaleTimeString()}</p>
        <p><strong>Eind Tijd:</strong> {new Date(onderhoud.eindTijd).toLocaleTimeString()}</p>
        <p><strong>Status:</strong> {onderhoud.status}</p>
        <p><strong>Reden:</strong> {onderhoud.reden}</p>
        <p><strong>Technieker:</strong> {onderhoud.technieker.voornaam} {onderhoud.technieker.achternaam}</p>
        <p><strong>Opmerkingen:</strong> {onderhoud.opmerkingen}</p>
      </AsyncData>
    </div>
  );
};