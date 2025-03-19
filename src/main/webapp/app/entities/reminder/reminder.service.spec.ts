import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import ReminderService from './reminder.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Reminder } from '@/shared/model/reminder.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('Reminder Service', () => {
    let service: ReminderService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new ReminderService();
      currentDate = new Date();
      elemDefault = new Reminder(123, 'AAAAAAA', currentDate, 'AAAAAAA', currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = {
          reminderTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a Reminder', async () => {
        const returnedFromService = {
          id: 123,
          reminderTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { reminderTime: currentDate, createdAt: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Reminder', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Reminder', async () => {
        const returnedFromService = {
          message: 'BBBBBB',
          reminderTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          repeatInterval: 'BBBBBB',
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };

        const expected = { reminderTime: currentDate, createdAt: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Reminder', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Reminder', async () => {
        const patchObject = { reminderTime: dayjs(currentDate).format(DATE_TIME_FORMAT), ...new Reminder() };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { reminderTime: currentDate, createdAt: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Reminder', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Reminder', async () => {
        const returnedFromService = {
          message: 'BBBBBB',
          reminderTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          repeatInterval: 'BBBBBB',
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { reminderTime: currentDate, createdAt: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Reminder', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Reminder', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Reminder', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
