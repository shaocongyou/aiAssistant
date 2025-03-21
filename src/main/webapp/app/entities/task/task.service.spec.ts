import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import TaskService from './task.service';
import { DATE_FORMAT, DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Task } from '@/shared/model/task.model';

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
  describe('Task Service', () => {
    let service: TaskService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new TaskService();
      currentDate = new Date();
      elemDefault = new Task(123, 'AAAAAAA', 'AAAAAAA', 'PENDING', currentDate, 0, currentDate, currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = {
          dueDate: dayjs(currentDate).format(DATE_FORMAT),
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
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

      it('should create a Task', async () => {
        const returnedFromService = {
          id: 123,
          dueDate: dayjs(currentDate).format(DATE_FORMAT),
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { dueDate: currentDate, createdAt: currentDate, updatedAt: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Task', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Task', async () => {
        const returnedFromService = {
          title: 'BBBBBB',
          description: 'BBBBBB',
          status: 'BBBBBB',
          dueDate: dayjs(currentDate).format(DATE_FORMAT),
          priority: 1,
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };

        const expected = { dueDate: currentDate, createdAt: currentDate, updatedAt: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Task', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Task', async () => {
        const patchObject = {
          title: 'BBBBBB',
          description: 'BBBBBB',
          updatedAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...new Task(),
        };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { dueDate: currentDate, createdAt: currentDate, updatedAt: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Task', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Task', async () => {
        const returnedFromService = {
          title: 'BBBBBB',
          description: 'BBBBBB',
          status: 'BBBBBB',
          dueDate: dayjs(currentDate).format(DATE_FORMAT),
          priority: 1,
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { dueDate: currentDate, createdAt: currentDate, updatedAt: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Task', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Task', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Task', async () => {
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
