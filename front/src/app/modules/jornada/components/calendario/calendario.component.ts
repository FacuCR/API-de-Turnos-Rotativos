import { Component } from '@angular/core';
import {
  localeEs,
  MbscCalendarEvent,
  MbscEventcalendarOptions,
  setOptions,
} from '@mobiscroll/angular';

setOptions({
  locale: localeEs,
  theme: 'ios',
  themeVariant: 'light',
});

@Component({
  selector: 'app-calendario',
  templateUrl: './calendario.component.html',
  styleUrls: ['./calendario.component.css'],
})
export class CalendarioComponent {
  shifts: MbscCalendarEvent[] = [
    {
      start: '2022-06-23T00:00',
      end: '2022-06-24T00:00',
      title: '6 horas',
      resource: 2,
      slot: 1,
    },
    {
      start: '2022-06-23T07:00',
      end: '2022-06-23T13:00',
      title: '07:00 - 13:00',
      resource: 3,
      slot: 1,
    },
    {
      start: '2022-06-23T07:00',
      end: '2022-06-23T13:00',
      title: '07:00 - 13:00',
      resource: 1,
      slot: 1,
    },
  ];

  calendarOptions: MbscEventcalendarOptions = {
    view: {
      timeline: {
        type: 'week',
        eventList: true,
        startDay: 1,
      },
    },
    resources: [
      {
        id: 1,
        name: 'Ryan',
        user: 'Cloud System Engineer',
      },
      {
        id: 2,
        name: 'Kate',
        user: 'Help Desk Specialist',
      },
      {
        id: 3,
        name: 'John',
        user: 'Application Developer',
      },
    ],
    colors: [
      {
        background: '#a5ceff4d',
        slot: 1,
        recurring: {
          repeat: 'weekly',
          weekDays: 'MO,TU,WE,TH,FR,SA,SU',
        },
      },
      {
        background: '#f7f7bb4d',
        slot: 2,
        recurring: {
          repeat: 'weekly',
          weekDays: 'MO,TU,WE,TH,FR,SA,SU',
        },
      },
      {
        background: '#ce448b44',
        slot: 3,
        recurring: {
          repeat: 'weekly',
          weekDays: 'MO,TU,WE,TH,FR,SA,SU',
        },
      },
    ],
    slots: [
      {
        id: 1,
        name: 'Mañana',
      },
      {
        id: 2,
        name: 'Tarde',
      },
      {
        id: 3,
        name: 'Noche',
      },
    ],
    onEventClick: (args: any) => {
        console.log("click en " + args.resource);
    }
  };

  backgroundDelTurno(id: number): string {
    if (id === 1) {
      return '#a5ceff4d';
    } else if (id === 2) {
      return '#f7f7bb4d';
    } else {
      return '#ce448b4d';
    }
  }
}
