import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import MoodTracker from './mood-tracker.vue';
import MoodTrackerService from './mood-tracker.service';
import AlertService from '@/shared/alert/alert.service';

type MoodTrackerComponentType = InstanceType<typeof MoodTracker>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('MoodTracker Management Component', () => {
    let moodTrackerServiceStub: SinonStubbedInstance<MoodTrackerService>;
    let mountOptions: MountingOptions<MoodTrackerComponentType>['global'];

    beforeEach(() => {
      moodTrackerServiceStub = sinon.createStubInstance<MoodTrackerService>(MoodTrackerService);
      moodTrackerServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          moodTrackerService: () => moodTrackerServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        moodTrackerServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(MoodTracker, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(moodTrackerServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.moodTrackers[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(MoodTracker, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(moodTrackerServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: MoodTrackerComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(MoodTracker, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        moodTrackerServiceStub.retrieve.reset();
        moodTrackerServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        moodTrackerServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(moodTrackerServiceStub.retrieve.called).toBeTruthy();
        expect(comp.moodTrackers[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(moodTrackerServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        moodTrackerServiceStub.retrieve.reset();
        moodTrackerServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(moodTrackerServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.moodTrackers[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(moodTrackerServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        moodTrackerServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeMoodTracker();
        await comp.$nextTick(); // clear components

        // THEN
        expect(moodTrackerServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(moodTrackerServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
